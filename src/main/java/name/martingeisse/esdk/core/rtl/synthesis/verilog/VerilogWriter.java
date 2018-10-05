/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.Matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *
 */
public class VerilogWriter {

	private final PrintWriter out;
	private final AuxiliaryFileFactory auxiliaryFileFactory;
	private Map<RtlClockNetwork, String> clockNames;
	private Map<RtlSignal, String> namedSignals;
	private int instanceCounter = 0;
	private int memoryCounter = 0;

	public VerilogWriter(PrintWriter out, AuxiliaryFileFactory auxiliaryFileFactory) {
		this.out = out;
		this.auxiliaryFileFactory = auxiliaryFileFactory;
	}

	// note: the namedSignals must include mappings for all input pins
	void prepare(Map<RtlClockNetwork, String> clockNames, Map<RtlSignal, String> namedSignals) {
		this.clockNames = clockNames;
		this.namedSignals = namedSignals;
	}

	public PrintWriter getOut() {
		return out;
	}


	//
	// naming
	//

	public String getSignalName(RtlSignal signal) {
		String name = namedSignals.get(signal);
		if (name == null) {
			throw new IllegalArgumentException("could not find name for signal: " + signal);
		}
		return name;
	}

	public void printSignalName(RtlSignal signal) {
		out.print(getSignalName(signal));
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


	public void printProceduralSignalName(RtlProceduralSignal signal) {
		String name = namedSignals.get(signal);
		if (name == null) {
			throw new IllegalArgumentException("no name allocated for procedural signal " + signal);
		}
		out.print(name);
	}

	//
	// module instances
	//

	public String newInstanceName() {
		instanceCounter++;
		return "instance" + instanceCounter;
	}

	//
	// memories
	//

	public String newMemoryName() {
		memoryCounter++;
		return "memory" + memoryCounter;
	}

	//
	// memories and MIFs
	//

	public void generateMif(String filename, Matrix matrix) throws IllegalArgumentException, IOException {
		if (filename == null) {
			throw new IllegalArgumentException("filename argument is null");
		}
		if (matrix == null) {
			throw new IllegalArgumentException("matrix argument is null");
		}
		try (OutputStream outputStream = auxiliaryFileFactory.create(filename)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII)) {
				matrix.writeToMif(new PrintWriter(outputStreamWriter));
			}
		}
	}

}
