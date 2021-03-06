/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.RealVerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;

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

	protected abstract String getName(RtlItem item);

	public void printName(RtlItem item) {
		String name = getName(item);
		if (name == null) {
			throw new IllegalArgumentException("no verilog name has been assigned to item: " + item);
		}
		print(name);
	}

	/**
	 * Prints the expression to use for a signal at a point where the signal gets used.
	 */
	public void printSignal(RtlSignal signal) {
		if (signal == null) {
			throw new IllegalArgumentException("signal argument is null");
		}
		String name = getName(signal.getRtlItem());
		if (name == null) {
			printImplementationExpression(signal);
		} else {
			print(name);
		}
	}

	/**
	 * Prints the expression to use for a procedural memory at a point where the memory gets used.
	 */
	public void printMemory(RtlProceduralMemory memory) {
		if (memory == null) {
			throw new IllegalArgumentException("memory argument is null");
		}
		String name = getName(memory);
		if (name == null) {
			throw new RuntimeException("could not determine verilog name for memory");
		}
		print(name);
	}

	/**
	 * This method should normally not be called directly since {@link #printSignal(RtlSignal)}
	 * is usually the right method to use. This method works similarly, but for named signals,
	 * it prints the defining expression, not the name.
	 */
	void printImplementationExpression(RtlSignal signal) {
		signal.printVerilogImplementationExpression(new RealVerilogExpressionWriter(this) {

			@Override
			public void internalPrintSignal(RtlSignal signal, VerilogExpressionNesting nesting) {
				VerilogWriter.this.printSignal(signal);
			}

			@Override
			public void internalPrintMemory(RtlProceduralMemory memory) {
				VerilogWriter.this.printMemory(memory);
			}

		});
	}

}
