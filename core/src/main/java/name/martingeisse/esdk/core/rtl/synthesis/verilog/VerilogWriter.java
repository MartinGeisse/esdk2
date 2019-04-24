/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

import java.io.PrintWriter;
import java.io.Writer;

/**
 *
 */
public abstract class VerilogWriter extends PrintWriter {

	private int indentation = 0;

	public VerilogWriter(Writer out) {
		super(out);
	}

	public void startIndentation() {
		indentation++;
	}

	public void endIndentation() {
		indentation--;
	}

	public void indent() {
		for (int i = 0; i < indentation; i++) {
			print('\t');
		}
	}

	protected abstract String getSignalName(RtlSignal signal);

	/**
	 * Prints the expression to use for a signal at a point where the signal gets used.
	 */
	public void print(RtlSignal signal) {
		if (signal == null) {
			throw new IllegalArgumentException("signal argument is null");
		}
		String name = getSignalName(signal);
		if (name == null) {
			printImplementationExpression(signal);
		} else {
			print(name);
		}
	}

	/**
	 * This method should normally not be called directly since {@link #print(RtlSignal)}
	 * is usually the right method to use. This method works similarly, but for named signals,
	 * it prints the defining expression, not the name.
	 */
	void printImplementationExpression(RtlSignal signal) {
		signal.printVerilogImplementationExpression(new VerilogExpressionWriter() {

			@Override
			public VerilogExpressionWriter print(String s) {
				VerilogWriter.this.print(s);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(int i) {
				VerilogWriter.this.print(i);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(char c) {
				VerilogWriter.this.print(c);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(RtlSignal signal, VerilogExpressionNesting nesting) {
				VerilogWriter.this.print(signal);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(RtlProceduralMemory memory) {
				// TODO
				throw new UnsupportedOperationException("not yet implemented");
			}

		});
	}

}
