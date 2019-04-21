/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

/**
 * (4 x 8M) x 16 Bit = 32M * 16 Bit = 64 MB = 16MW
 * 1 W = 32 Bits
 */
public class AlgorithmTestMain {

	private static int[] memory = new int[16 * 1024 * 1024];

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
			};
			x = (5 * x + 1) % 17;
		}

	}

}
