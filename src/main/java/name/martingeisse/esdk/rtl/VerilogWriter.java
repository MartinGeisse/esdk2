/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class VerilogWriter {

	private final PrintWriter out;
	private int indentation = 0;
	private final Map<RtlClockNetwork, String> clockNames = new HashMap<>();
	private final Map<RtlSignal, String> signalNames = new HashMap<>();

	public VerilogWriter(PrintWriter out) {
		this.out = out;
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

	public String getClockName(RtlClockNetwork clockNetwork) {
		return getNameInternal(clockNames, clockNetwork, "clk");
	}

	public void printClockName(RtlClockNetwork clockNetwork) {
		out.print(getClockName(clockNetwork));
	}

	public String getSignalName(RtlSignal signal) {
		return getNameInternal(signalNames, signal, "s");
	}

	public void printSignalName(RtlSignal signal) {
		out.print(getSignalName(signal));
	}

	private <T> String getNameInternal(Map<T, String> nameMap, T object, String prefix) {
		String name = nameMap.get(object);
		if (name == null) {
			name = prefix + nameMap.size();
			nameMap.put(object, name);
		}
		return name;
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
		out.println("endmodule;");
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

}
