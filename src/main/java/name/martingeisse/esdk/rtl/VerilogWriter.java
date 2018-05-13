/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import java.io.PrintWriter;

/**
 *
 */
public class VerilogWriter {

	private final PrintWriter out;
	private int indentation = 0;

	public VerilogWriter(PrintWriter out) {
		this.out = out;
	}

	// TODO ports
	public void writeIntro(String moduleName) {
		out.println("`default_nettype none");
		out.println("`timescale 1ns / 1ps");
		out.println();
		out.println("module " + moduleName + "(");
		startIndentation();
		indent();
		out.println("clk");
		endIndentation();
		out.println(");");
		out.println();
		out.println("input clk;");
		out.println();
	}

	public void writeOutro() {
		out.println("endmodule;");
		out.println();
	}

	public void startIndentation() {
		indentation++;
	}

	public void endIndentation() {
		indentation--;
	}

	public void indent() {
		for (int i = 0; i < indentation; i++) {
			out.print('\t');
		}
	}

}
