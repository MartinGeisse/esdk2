/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.riscv.instruction.gui;

import name.martingeisse.esdk.riscv.instruction.InstructionLevelRiscv;
import name.martingeisse.esdk.riscv.instruction.io.IoUnit;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class Main {

	private final MyCpu cpu;
	private final int[] memory;
	private boolean stopped = false;

	private Main() {
		this.cpu = new MyCpu();
		this.memory = new int[64 * 1024 * 1024];
	}

	public static void main(String[] args) throws Exception {

		Main simulator = new Main();
		simulator.loadProgram(new File("riscv/resource/gfx-program/build/program.bin"));

		PixelPanel pixelPanel = new PixelPanel(simulator.memory);
		JFrame frame = new JFrame("Pixel Display");
		frame.add(pixelPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> pixelPanel.repaint()).start();

		simulator.loop();

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
				memory[index] = first | (readByteEofSafe(in) << 8) | (readByteEofSafe(in) << 16) | (readByteEofSafe(in) << 24);
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
					if (wordAddress < 0 || wordAddress >= memory.length) {
						throw new IllegalArgumentException("illegal address for instruction fetch: " + wordAddress);
					}
					return memory[wordAddress];
				}

				public int read(int wordAddress) {
					if (wordAddress < 0) {
						return ioRead(wordAddress);
					} else if (wordAddress >= memory.length) {
						throw new IllegalArgumentException("illegal address for reading: " + wordAddress);
					} else {
						return memory[wordAddress];
					}
				}

				public void write(int wordAddress, int data, int byteMask) {
					if (byteMask != (byteMask & 15)) {
						throw new IllegalArgumentException("illegal byte mask for writing: " + wordAddress);
					}
					if (wordAddress < 0) {
						ioWrite(wordAddress, data, byteMask);
					} else if (wordAddress >= memory.length) {
						throw new IllegalArgumentException("illegal address for writing: " + wordAddress);
					} else {
						if (byteMask == 15) {
							memory[wordAddress] = data;
						} else {
							writeHelper(wordAddress, data, byteMask & 1, 0x000000ff);
							writeHelper(wordAddress, data, byteMask & 2, 0x0000ff00);
							writeHelper(wordAddress, data, byteMask & 4, 0x00ff0000);
							writeHelper(wordAddress, data, byteMask & 8, 0xff000000);
						}
					}
				}

				private void writeHelper(int wordAddress, int data, int selectedByteMask, int bitMask) {
					if (selectedByteMask != 0) {
						memory[wordAddress] = (memory[wordAddress] & ~bitMask) | (data & bitMask);
					}
				}

			});
		}
	}

	private int ioRead(int wordAddress) {
		return 0;
	}

	private void ioWrite(int wordAddress, int data, int byteMask) {
		if (wordAddress == -1) {
			stopped = true;
		} else if (wordAddress == -2) {
			System.out.println("debugWrite: " + data);
		}
	}

}
