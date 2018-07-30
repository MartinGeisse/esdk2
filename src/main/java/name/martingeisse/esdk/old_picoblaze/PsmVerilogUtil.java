/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze;

import java.io.IOException;

/**
 * Utility methods to generate Verilog code from PSM instructions.
 *
 * Block RAM initialization: We have 1024 instructions, with 18 bits each.
 * The Block RAM is initialized as (64*256 data bits + 8*256 parity bits).
 * Each instruction is stored in the block RAM as 16 contiguous data bits
 * and 2 contiguous parity bits, so the organization could be written as:
 *
 *   (64 * 16 * 16 + 8 * 128 * 2) bits
 *
 * The initialization is written in hex digits, each comprising 4 bits. The
 * hex digits in one row are written in reverse order, with 64 data rows
 * and 8 parity rows. This yields:
 *
 *   data:
 *     64 rows
 *     16 instructions per row
 *     4 digits per instruction
 *     4 bits per digit
 *
 *   parity:
 *     8 rows
 *     64 double-instructions per row
 *     1 digit per double-instruction
 *     4 bits per digit
 *
 */
public class PsmVerilogUtil {

	static void writeDigit(final StringBuilder builder, final int digit) {
		if (digit < 10) {
			builder.append((char)('0' + digit));
		} else {
			builder.append((char)('A' + digit - 10));
		}
	}

	static void writeDataRow(final StringBuilder builder, final int[] instructions, final int rowNumber) throws IllegalArgumentException, IOException {
		for (int localInstructionIndex = 15; localInstructionIndex >= 0; localInstructionIndex--) {
			final int globalInstructionIndex = (rowNumber * 16 + localInstructionIndex);
			final int instruction = (instructions[globalInstructionIndex] & 0x3ffff);
			for (int instructionDigitIndex = 3; instructionDigitIndex >= 0; instructionDigitIndex--) {
				final int instructionDigit = ((instruction >> (instructionDigitIndex * 4)) & 15);
				writeDigit(builder, instructionDigit);
			}
		}
	}

	static void writeParityRow(final StringBuilder builder, final int[] instructions, final int rowNumber) throws IllegalArgumentException, IOException {
		for (int localDoubleInstructionIndex = 63; localDoubleInstructionIndex >= 0; localDoubleInstructionIndex--) {
			final int instruction1 = (instructions[(rowNumber * 64 + localDoubleInstructionIndex) * 2 + 0] & 0x3ffff);
			final int instruction2 = (instructions[(rowNumber * 64 + localDoubleInstructionIndex) * 2 + 1] & 0x3ffff);
			final int instructionBits1 = ((instruction1 >> 16) & 3);
			final int instructionBits2 = ((instruction2 >> 16) & 3);
			final int digit = (instructionBits2 << 2) + instructionBits1;
			writeDigit(builder, digit);
		}
	}

	static void writeRows(final StringBuilder builder, final int[] instructions, final boolean parity, final String preRowNumber, final String preContents, final String postContents) throws IllegalArgumentException, IOException {
		final int rowCount = (parity ? 8 : 64);
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			builder.append(preRowNumber);
			writeDigit(builder, rowIndex >> 4);
			writeDigit(builder, rowIndex & 15);
			builder.append(preContents);
			if (parity) {
				writeParityRow(builder, instructions, rowIndex);
			} else {
				writeDataRow(builder, instructions, rowIndex);
			}
			builder.append(postContents);
			builder.append('\n');
		}
	}

	/**
	 * Generates a MIF for the instruction memory.
	 *
	 * @param instructions the encoded instructions. The size of this array must be 1024.
	 * @return the verilog code
	 * @throws IllegalArgumentException when the instructions argument is
	 * null or does not have exactly 1024 elements.
	 * @throws IOException on I/O errors
	 */
	public static String generateMif(final int[] instructions) throws IllegalArgumentException, IOException {
		if (instructions == null) {
			throw new IllegalArgumentException("instructions argument is null");
		}
		if (instructions.length != 1024) {
			throw new IllegalArgumentException("instructions argument has " + instructions.length + " elements, 1024 expected");
		}
		final StringBuilder builder = new StringBuilder();
		for (final int instruction : instructions) {
			final String hex = Integer.toHexString(instruction);
			final String zeros = "00000".substring(hex.length());
			builder.append(zeros).append(hex).append('\n');
		}
		return builder.toString();
	}

	/**
	 * Generates a verilog file for the instruction memory.
	 *
	 * @param moduleName the module name
	 * @return the verilog code
	 * @throws IllegalArgumentException when the instructions argument is
	 * null or does not have exactly 1024 elements.
	 * @throws IOException on I/O errors
	 */
	public static String generateMemoryVerilog(final String moduleName) throws IllegalArgumentException, IOException {
		final StringBuilder builder = new StringBuilder();
		builder.append("`default_nettype none\n");
		builder.append("`timescale 1ns / 1ps\n");
		builder.append("\n");
		builder.append("/**\n");
		builder.append(" * This is a PicoBlaze program memory.\n");
		builder.append(" */\n");
		builder.append("module " + moduleName + " (\n");
		builder.append("\t\t\n");
		builder.append("\t\t/** the clock signal **/\n");
		builder.append("\t\tinput clk,\n");
		builder.append("\t\t\n");
		builder.append("\t\t/** the current instruction address **/\n");
		builder.append("\t\tinput [9:0] address,\n");
		builder.append("\t\t\n");
		builder.append("\t\t/** the instruction **/\n");
		builder.append("\t\toutput reg [17:0] instruction\n");
		builder.append("\t\t\n");
		builder.append("\t);\n");
		builder.append("\t\n");
		builder.append("\treg [17:0] rom [1023:0];\n");
		builder.append("\tinitial $readmemh(\"" + moduleName + ".mif\", rom, 0, 1023);\n");
		builder.append("\talways @(posedge clk) begin\n");
		builder.append("\t\tinstruction <= rom[address];\n");
		builder.append("\tend\n");
		builder.append("\t\n");
		builder.append("endmodule\n");
		return builder.toString();
	}

}
