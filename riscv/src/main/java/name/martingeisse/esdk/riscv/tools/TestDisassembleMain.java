/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.riscv.tools;

import name.martingeisse.esdk.library.util.StringUtil;

/**
 *
 */
public class TestDisassembleMain {

	private static int[] values = {
	};

	public static void main(String[] args) {
		for (int wordAddress = 0; wordAddress < values.length; wordAddress++) {
			int byteAddress = wordAddress << 2;
			int wrongOrderInstruction = values[wordAddress];
			int instruction =
				((wrongOrderInstruction >> 24) & 0x000000ff) |
					((wrongOrderInstruction >> 8) & 0x0000ff00) |
					((wrongOrderInstruction << 8) & 0x00ff0000) |
					((wrongOrderInstruction << 24) & 0xff000000);
			System.out.println(StringUtil.toHexString32(byteAddress) + ": " +
				StringUtil.toHexString32(wrongOrderInstruction) + " -> " +
				InstructionDisassembler.disassemble(instruction));
		}
	}

}
