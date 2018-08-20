/**
 * Copyright (c) 2015 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.picoblaze.synthesis;

import java.io.IOException;

/**
 * Utility methods to generate Verilog code from PSM instructions. TODO generalize and move to RTL package
 */
public class PsmVerilogUtil {

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
