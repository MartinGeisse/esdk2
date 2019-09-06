/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.riscv.instruction.gui;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.riscv.instruction.InstructionLevelRiscv;
import name.martingeisse.esdk.riscv.instruction.io.IoUnit;
import name.martingeisse.esdk.riscv.rtl.Multicycle;
import name.martingeisse.esdk.riscv.rtl.ram.SimulatedRam;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class Main {

	private final MyCpu cpu;
	private final int[] smallMemory;
	private final int[] bigMemory;
	private boolean stopped = false;

	private Main() {
		this.cpu = new MyCpu();
		this.smallMemory = new int[4 * 1024];
		this.bigMemory = new int[16 * 1024 * 1024];
	}

	public static void main(String[] args) throws Exception {

		Main simulator = new Main();
		simulator.loadProgram(new File("riscv/resource/gfx-program/build/program.bin"));

		PixelPanel pixelPanel = new PixelPanel(simulator.bigMemory);
		JFrame frame = new JFrame("Pixel Display");
		frame.add(pixelPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> pixelPanel.repaint()).start();

		try {
			simulator.loop();
		} catch (Exception e) {
			throw new RuntimeException("error at PC = 0x" + Integer.toHexString(simulator.cpu.getPc()), e);
		}

		frame.setVisible(false);
		System.exit(0);

	}

	private void loadProgram(File file) throws Exception {
		try (FileInputStream in = new FileInputStream(file)) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				smallMemory[index] = first | (readByteEofSafe(in) << 8) | (readByteEofSafe(in) << 16) | (readByteEofSafe(in) << 24);
				index++;
			}
		}
	}

	private int readByteEofSafe(InputStream in) throws IOException {
		return Math.max(in.read(), 0);
	}

	private void loop() {
		while (!stopped) {
			cpu.step();
		}
	}

	private class MyCpu extends InstructionLevelRiscv {
		private MyCpu() {
			setIoUnit(new IoUnit() {

				public int fetchInstruction(int wordAddress) {
					return busRead(wordAddress);
				}

				public int read(int wordAddress) {
					return busRead(wordAddress);
				}

				public void write(int wordAddress, int data, int byteMask) {
					busWrite(wordAddress, data, byteMask);
				}

			});
		}
	}

	private int busRead(int wordAddress) {
		try {
			if (wordAddress < 0) {
				return bigMemory[wordAddress & 0x7fffffff];
			} else if (wordAddress < 0x40000000) {
				return smallMemory[wordAddress & 0x3fffffff];
			} else {
				return readSimulationDevice(wordAddress & 0x3fffffff);
			}
		} catch (Exception e) {
			throw new RuntimeException("error reading from word address 0x" + Integer.toHexString(wordAddress), e);
		}
	}

	private void busWrite(int wordAddress, int data, int byteMask) {
		try {
			if (wordAddress < 0) {
				writeMemory(bigMemory, wordAddress ^ 0x80000000, data, byteMask);
			} else if (wordAddress < 0x40000000) {
				writeMemory(smallMemory, wordAddress, data, byteMask);
			} else {
				writeSimulationDevice(wordAddress & 0x3fffffff, data);
			}
		} catch (Exception e) {
			throw new RuntimeException("error writing to word address 0x" + Integer.toHexString(wordAddress), e);
		}
	}

	private static void writeMemory(int[] memory, int wordAddress, int data, int byteMask) {
		if (byteMask == 15) {
			memory[wordAddress] = data;
		} else {
			writeHelper(memory, wordAddress, data, byteMask & 1, 0x000000ff);
			writeHelper(memory, wordAddress, data, byteMask & 2, 0x0000ff00);
			writeHelper(memory, wordAddress, data, byteMask & 4, 0x00ff0000);
			writeHelper(memory, wordAddress, data, byteMask & 8, 0xff000000);
		}
	}

	private static void writeHelper(int[] memory, int wordAddress, int data, int selectedByteMask, int bitMask) {
		if (selectedByteMask != 0) {
			memory[wordAddress] = (memory[wordAddress] & ~bitMask) | (data & bitMask);
		}
	}

	private int readSimulationDevice(int wordAddress) {
		switch (wordAddress) {

			case 0:
				// return 1 to show that this is a simulation
				return 1;

			default:
				return 0;

		}
	}

	private void writeSimulationDevice(int wordAddress, int data) {
		switch (wordAddress) {

			case 0:
				stopped = true;
				break;

			case 1:
				debugPrint(data);
				break;

			case 2:
				memoryHelper(data);
				break;

		}
	}

	private void debugPrint(int subcode) {
		System.out.print("OUT:        ");
		int a0 = cpu.getRegister(10);
		int a1 = cpu.getRegister(11);
		switch (subcode) {

			case 0:
				System.out.println(readZeroTerminatedMemoryString(a0));
				break;

			case 1: {
				System.out.println(readZeroTerminatedMemoryString(a0) + ": " + a1 + " (0x" + Integer.toHexString(a1) + ")");
				break;
			}

			default:
				System.out.println("???");
		}
	}

	public byte readMemoryByte(int address) {
		int wordAddress = (address & 0x0fffffff) >> 2;
		int wordValue = busRead(wordAddress);
		int byteOffset = (address & 3);
		return (byte)(wordValue >> (byteOffset * 8));
	}

	public byte[] readMemoryBytes(int startAddress, int count) {
		byte[] result = new byte[count];
		for (int i = 0; i < count; i++) {
			result[i] = readMemoryByte(startAddress + i);
		}
		return result;
	}

	public String readMemoryString(int startAddress, int count) {
		return new String(readMemoryBytes(startAddress, count), StandardCharsets.ISO_8859_1);
	}

	public String readZeroTerminatedMemoryString(int startAddress) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		while (true) {
			byte b = readMemoryByte(startAddress);
			if (b == 0) {
				break;
			}
			stream.write(b);
			startAddress++;
		}
		return new String(stream.toByteArray(), StandardCharsets.ISO_8859_1);
	}

	private void memoryHelper(int subcode) {
		int a0 = cpu.getRegister(10) & 0x0fffffff;
		int a1 = cpu.getRegister(11);
		int a2 = cpu.getRegister(12);
		switch (subcode) {

			// fill words
			case 0: {
				int wordAddress = (a0 >> 2);
				for (int i = 0; i < a2; i++) {
					bigMemory[wordAddress + i] = a1;
				}
				break;
			}

			default:
				System.out.println("invalid memoryHelper subcode: " + subcode);
		}
	}

}
