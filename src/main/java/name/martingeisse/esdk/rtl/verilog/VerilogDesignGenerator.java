/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.verilog;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.block.RtlBlock;
import name.martingeisse.esdk.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.rtl.pin.RtlPin;
import name.martingeisse.esdk.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.rtl.signal.RtlSignal;
import name.martingeisse.esdk.rtl.signal.RtlVectorSignal;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class VerilogDesignGenerator {

	private final VerilogWriter out;
	private final RtlDesign design;
	private final String name;

	private Set<RtlSignal> allSignals;
	private Map<RtlSignal, String> declaredSignals;
	private VerilogExpressionWriter dryRunExpressionWriter;


	public VerilogDesignGenerator(PrintWriter out, RtlDesign design, String name) {
		this.out = new VerilogWriter(out);
		this.design = design;
		this.name = name;
	}

	public void generate() {
		analyzeSignals();
		out.prepare(new HashMap<>(), declaredSignals);
		out.printIntro(name, design.getPins());
		printSignalDeclarations();
		out.getOut().println();
		printBlocks();
		out.getOut().println();
		out.printOutro();
	}

	//
	// analysis
	//

	private void analyzeSignals() {
		allSignals = new HashSet<>();
		declaredSignals = new HashMap<>();
		dryRunExpressionWriter = new VerilogExpressionWriter() {

			@Override
			public VerilogExpressionWriter print(String s) {
				return this;
			}

			@Override
			public VerilogExpressionWriter print(int i) {
				return this;
			}

			@Override
			public VerilogExpressionWriter print(char c) {
				return this;
			}

			@Override
			public VerilogExpressionWriter print(RtlSignal subSignal, VerilogExpressionNesting subNesting) {
				analyzeSignal(subSignal, subNesting);
				return this;
			}

			@Override
			public VerilogExpressionWriter printProceduralSignalName(RtlProceduralSignal signal) {
				return this;
			}

		};

		// procedural signals must be declared
		for (RtlBlock block : design.getBlocks()) {
			for (RtlProceduralSignal signal : block.getProceduralSignals()) {
				allSignals.add(signal);
				declareSignal(signal);
			}
		}

		// analyze output and output-enable signals for signals to extract
		for (RtlPin pin : design.getPins()) {
			if (pin instanceof RtlOutputPin) {
				RtlOutputPin outputPin = (RtlOutputPin)pin;
				analyzeSignal(outputPin.getOutputSignal(), VerilogExpressionNesting.ALL);
			} else if (pin instanceof RtlBidirectionalPin) {
				RtlBidirectionalPin bidirectionalPin = (RtlBidirectionalPin)pin;
				analyzeSignal(bidirectionalPin.getOutputSignal(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
				analyzeSignal(bidirectionalPin.getOutputEnableSignal(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
			}
		}

		// analyze signal "expressions" in blocks for signals to extract
		for (RtlBlock block : design.getBlocks()) {
			block.getStatements().printExpressionsDryRun(dryRunExpressionWriter);
			if (block instanceof RtlClockedBlock) {
				((RtlClockedBlock) block).getInitializerStatements().printExpressionsDryRun(dryRunExpressionWriter);
			}
		}

	}

	private void analyzeSignal(RtlSignal signal, VerilogExpressionNesting nesting) {

		// extract all signals that are used in more than one place. Those have been analyzed already.
		if (!allSignals.add(signal)) {
			declareSignal(signal);
			return;
		}

		// also extract signals that do not comply with the current nesting level
		boolean compliesWithNesting = signal.compliesWith(nesting);
		if (!compliesWithNesting) {
			declareSignal(signal);
		}

		// Analyze signals for sub-expressions by calling a "dry-run" printing process. While this sounds more complex,
		// it concentrates the complexity here, in one place, and simplifies the RtlSignal implementations.
		signal.printVerilogExpression(dryRunExpressionWriter);

	}

	private String declareSignal(RtlSignal signal) {
		String name = declaredSignals.get(signal);
		if (name == null) {
			name = "s" + declaredSignals.size();
			declaredSignals.put(signal, name);
		}
		return name;
	}

	public enum VerilogExpressionNesting {
		ALL,
		SELECTIONS_SIGNALS_AND_CONSTANTS,
		SIGNALS_AND_CONSTANTS
	}

	//
	// code generation
	//

	private void printSignalDeclarations() {
		for (Map.Entry<RtlSignal, String> signalEntry : declaredSignals.entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			String signalName = signalEntry.getValue();
			if (signal instanceof RtlProceduralSignal) {
				out.getOut().print("reg");
			} else {
				out.getOut().print("wire");
			}
			if (signal instanceof RtlVectorSignal) {
				RtlVectorSignal vectorSignal = (RtlVectorSignal)signal;
				out.getOut().print('[');
				out.getOut().print(vectorSignal.getWidth() - 1);
				out.getOut().print(":0] ");
			} else if (signal instanceof RtlBitSignal) {
				out.getOut().print(' ');
			} else {
				throw new RuntimeException("signal is neither a bit signal nor a vector signal: " + signal);
			}
			out.getOut().print(signalName);
			out.getOut().println(';');
		}
	}

	private void printBlocks() {
		for (RtlBlock block : design.getBlocks()) {
			block.printVerilogBlocks(out);
		}
	}
}
