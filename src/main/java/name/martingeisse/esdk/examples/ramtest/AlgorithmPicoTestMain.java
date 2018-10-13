/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

/**
 * (4 x 8M) x 16 Bit = 32M * 16 Bit = 64 MB = 16MW
 * 1 W = 32 Bits
 */
public class AlgorithmPicoTestMain {

	private static final int ADDRESS_MASK = 0x00ffffff;
	private static int[] memory = new int[16 * 1024 * 1024];

	private int addressRegister = 0;
	private int writeDataRegister = 0;
	private int readDataRegister = 0;

	public static void main(String[] args) {

		int x;

		x = 9;
		for (int i = 0; i < memory.length; i++) {
			memory[i] = x;
			x = (5 * x + 1) % 17;
		}

		x = 9;
		for (int i = 0; i < memory.length; i++) {
			if (memory[i] != x) {
				System.out.println("error at " + i + ": " + memory[i]);
			}
			x = (5 * x + 1) % 17;
		}

	}

	private void write(int port, int data) {
		int shift = (port & 3) * 8;
		int mask = ~(0xff << shift);
		int shiftedData = (data & 0xff) << shift;
		if ((port & 5) == 0) {
			if ((port & 6) == 0) {
				addressRegister = (addressRegister & mask) | shiftedData;
			} else {
				if ((data & 1) == 0) {
					memory[addressRegister & ADDRESS_MASK] = writeDataRegister;
				} else {
					readDataRegister = [addressRegister & ADDRESS_MASK];
				}
			}
		} else {
			if ((port & 6) == 0) {
				writeDataRegister = (writeDataRegister & mask) | shiftedData;
			} else {
				readDataRegister = (readDataRegister & mask) | shiftedData;
			}
		}
	}

	private byte read(int port) {
		int value;
		if ((port & 5) == 0) {
			value = addressRegister;
		} else if ((port & 6) == 0) {
			value = writeDataRegister;
		} else {
			value = readDataRegister;
		}
		int shift = (port & 3) * 8;
		return (byte) (value >> shift);
	}

}
