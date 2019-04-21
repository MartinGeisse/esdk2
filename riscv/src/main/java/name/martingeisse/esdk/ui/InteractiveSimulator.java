/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.ui;

import name.martingeisse.esdk.riscv.InstructionLevelRiscv;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class InteractiveSimulator {

	private final MyCpu cpu;
	private final int[] memory;
	private boolean stopped = false;

	public InteractiveSimulator() {
		this.cpu = new MyCpu();
		this.memory = new int[1024 * 1024];
	}

	public static void main(String[] args) throws Exception {
		InteractiveSimulator simulator = new InteractiveSimulator();
		simulator.loadProgram(new File("resource/riscv-test/build/I-ADDI-01.bin"));
		simulator.loop();
	}

	public void loadProgram(File file) throws Exception {
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
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

	public void loop() {
		try (InputStreamReader inputStreamReader = new InputStreamReader(System.in, StandardCharsets.ISO_8859_1)) {
			try (LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader)) {
				while (!stopped) {
					showCpuStatus();
					String line = lineNumberReader.readLine();
					if (line == null) {
						stopped = true;
					} else {
						consumeLine(line.trim());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void consumeLine(String line) {
		if (line.equals("s")) {
			cpu.step();
		} else {
			System.out.println("unknown command");
		}
	}

	private void showCpuStatus() {
		System.out.println();
		System.out.print("pc: " + cpu.getPc() + "        ");
		{
			int instruction = cpu.fetchInstruction(cpu.getPc() >> 2);
			for (int i = 31; i >= 0; i--) {
				System.out.print(((instruction >> i) & 1) == 0 ? '0' : '1');
				if (i % 4 == 0) {
					System.out.print(' ');
				}
			}
		}
		System.out.println();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			builder.append("x").append(i).append(": ");
			int value = cpu.getRegister(i);
			for (int j = 28; j >= 0; j-= 4) {
				int digit = (value >> j) & 0xf;
				builder.append((char)(digit < 10 ? (digit + '0') : (digit - 10 + 'a')));
			}
			while (builder.length() % 16 != 0) {
				builder.append(' ');
			}
			if (i % 8 == 7) {
				builder.append('\n');
			}
		}
		System.out.println(builder);
	}

	private void handleOutputValue(int value) {
		System.out.println(value + " / " + Integer.toHexString(value));
	}

	public class MyCpu extends InstructionLevelRiscv {

		public int fetchInstruction(int wordAddress) {
			if (wordAddress < 0 || wordAddress >= memory.length) {
				throw new IllegalArgumentException("illegal address for instruction fetch: " + wordAddress);
			}
			return memory[wordAddress];
		}

		public int read(int wordAddress) {
			if (wordAddress < 0 || wordAddress >= memory.length) {
				throw new IllegalArgumentException("illegal address for reading: " + wordAddress);
			}
			return memory[wordAddress];
		}

		public void write(int wordAddress, int data, int byteMask) {
			if (byteMask != (byteMask & 15)) {
				throw new IllegalArgumentException("illegal byte mask for writing: " + wordAddress);
			}
			if (wordAddress == -8) {
				handleOutputValue(data);
				return;
			}
			if (wordAddress == -9) {
				stopped = true;
				return;
			}
			if (wordAddress < 0 || wordAddress >= memory.length) {
				throw new IllegalArgumentException("illegal address for writing: " + wordAddress);
			}
			if (byteMask == 15) {
				memory[wordAddress] = data;
				return;
			}
			writeHelper(wordAddress, data, byteMask & 1, 0x000000ff);
			writeHelper(wordAddress, data, byteMask & 2, 0x0000ff00);
			writeHelper(wordAddress, data, byteMask & 4, 0x00ff0000);
			writeHelper(wordAddress, data, byteMask & 8, 0xff000000);
		}

		private void writeHelper(int wordAddress, int data, int selectedByteMask, int bitMask) {
			if (selectedByteMask != 0) {
				memory[wordAddress] = (memory[wordAddress] & ~bitMask) | (data & bitMask);
			}
		}

	}

}
