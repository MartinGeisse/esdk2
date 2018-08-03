/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

import java.io.PrintWriter;
import java.util.Map;

/**
 *
 */
public class VerilogWriter {

	private final PrintWriter out;
	private int indentation = 0;
	private Map<RtlClockNetwork, String> clockNames;
	private Map<RtlSignal, String> declaredSignals;

	public VerilogWriter(PrintWriter out) {
		this.out = out;
	}

	// note: the declaredSignals must include mappings for all input pins
	void prepare(Map<RtlClockNetwork, String> clockNames, Map<RtlSignal, String> declaredSignals) {
		this.clockNames = clockNames;
		this.declaredSignals = declaredSignals;
	}

	public PrintWriter getOut() {
		return out;
	}

	//
	// indentation
	//

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

	//
	// naming
	//

	public String getSignalName(RtlSignal signal) {
		String name = declaredSignals.get(signal);
		if (name == null) {
			throw new IllegalArgumentException("could not find name for signal: " + signal);
		}
		return name;
	}

	public void printSignalName(RtlSignal signal) {
		out.print(getSignalName(signal));
	}

	//
	// module frame
	//

	public void printIntro(String moduleName, Iterable<RtlPin> pins) {
		boolean hasPins = pins.iterator().hasNext();
		out.println("`default_nettype none");
		out.println("`timescale 1ns / 1ps");
		out.println();
		out.println("module " + moduleName + "(");
		if (hasPins) {
			startIndentation();
			boolean first = true;
			for (RtlPin pin : pins) {
				if (first) {
					first = false;
				} else {
					out.println(',');
				}
				indent();
				out.print(pin.getNetName());
			}
			out.println();
			endIndentation();
		}
		out.println(");");
		out.println();
		if (hasPins) {
			for (RtlPin pin : pins) {
				out.println(pin.getVerilogDirectionKeyword() + ' ' + pin.getNetName() + ';');
			}
			out.println();
		}
	}

	public void printOutro() {
		out.println("endmodule");
		out.println();
	}

	//
	// procedural blocks
	//

	public void startProceduralInitialBlock() {
		indent();
		out.println("initial begin");
		startIndentation();
	}

	public void endProceduralInitialBlock() {
		endIndentation();
		indent();
		out.println("end");
	}

	public void startProceduralAlwaysBlock(String trigger) {
		indent();
		out.println("always @(" + trigger + ") begin");
		startIndentation();
	}

	public void endProceduralAlwaysBlock() {
		endIndentation();
		indent();
		out.println("end");
	}

	//
	// expressions and assignment targets
	//

	/**
	 * Prints the expression to use for a signal at a point where the signal gets used.
	 */
	public void printExpression(RtlSignal signal) {
		String name = declaredSignals.get(signal);
		if (name == null) {
			printDefiningExpression(signal);
		} else {
			out.print(name);
		}
	}

	/**
	 * This method should normally not be called directly since {@link #printExpression(RtlSignal)}
	 * is usually the right method to use. This method works similarly, but for declared signals,
	 * it prints the defining expression, not the declared name.
	 */
	public void printDefiningExpression(RtlSignal signal) {
		signal.printVerilogExpression(new VerilogExpressionWriter() {

			@Override
			public VerilogExpressionWriter print(String s) {
				out.print(s);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(int i) {
				out.print(i);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(char c) {
				out.print(c);
				return this;
			}

			@Override
			public VerilogExpressionWriter print(RtlSignal signal, VerilogGenerator.VerilogExpressionNesting nesting) {
				printExpression(signal);
				return this;
			}

			@Override
			public VerilogExpressionWriter printProceduralSignalName(RtlProceduralSignal signal) {
				VerilogWriter.this.printProceduralSignalName(signal);
				return this;
			}

		});
	}

	public void printProceduralSignalName(RtlProceduralSignal signal) {
		String name = declaredSignals.get(signal);
		if (name == null) {
			throw new IllegalArgumentException("no name allocated for procedural signal " + signal);
		}
		out.print(name);
	}

}
