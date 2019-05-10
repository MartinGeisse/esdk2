/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.tools;

import name.martingeisse.esdk.library.util.StringUtil;

/**
 *
 */
public class TestDisassembleMain {

	private static int[] values = {
		0x6ff01fff,

//		0x97000000, 0x93800000, 0x17010000, 0x13010100,
//		0x83a10000, 0x23203100, 0x6ff09ffe, 0x13000000,
//		0x13000000, 0x13000000, 0x00000000, 0x13000000,
//		0x13000000, 0x13000000, 0xffffffff, 0x00000000


//								0x97000000, 0x93800000,
//		0x17010000, 0x13010100, 0x638a2000, 0x03a10000,
//		0x232020fe, 0x93804000, 0x6ff09ffe, 0x232e00fc

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
