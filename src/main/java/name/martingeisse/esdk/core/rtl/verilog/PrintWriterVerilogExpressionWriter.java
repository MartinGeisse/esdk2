/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.verilog;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

import java.io.PrintWriter;

/**
 * Makes a {@link PrintWriter} behave as a {@link VerilogExpressionWriter}. Writing nested expressions is not supported.
 */
public final class PrintWriterVerilogExpressionWriter implements VerilogExpressionWriter {

	private final PrintWriter printWriter;

	public PrintWriterVerilogExpressionWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}

	@Override
	public VerilogExpressionWriter print(String s) {
		printWriter.print(s);
		return this;
	}

	@Override
	public VerilogExpressionWriter print(int i) {
		printWriter.print(i);
		return this;
	}

	@Override
	public VerilogExpressionWriter print(char c) {
		printWriter.print(c);
		return this;
	}

	@Override
	public VerilogExpressionWriter print(RtlSignal signal, VerilogGenerator.VerilogExpressionNesting nesting) {
		throw new UnsupportedOperationException();
	}

	@Override
	public VerilogExpressionWriter printProceduralSignalName(RtlProceduralSignal signal) {
		throw new UnsupportedOperationException();
	}

}
