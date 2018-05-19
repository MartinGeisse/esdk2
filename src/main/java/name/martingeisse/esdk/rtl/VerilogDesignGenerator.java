/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import name.martingeisse.esdk.rtl.statement.RtlStatement;

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
	private Set<RtlSignal> declaredSignals;

	public VerilogDesignGenerator(VerilogWriter out, RtlDesign design, String name) {
		this.out = out;
		this.design = design;
		this.name = name;
	}

	public void generate() {
		analyzeSignals();
		out.printIntro(name, design.getPins());
		printSignalDeclarations();
		out.printOutro();
	}

	private void analyzeSignals() {
		allSignals = new HashSet<>();
		declaredSignals = new HashSet<>();

		// procedural signals must be declared
		for (RtlBlock block : design.getBlocks()) {
			for (RtlProceduralSignal signal : block.getProceduralSignals()) {
				allSignals.add(signal);
				declaredSignals.add(signal);
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
			analyzeSignals(block.getStatements());
			if (block instanceof RtlClockedBlock) {
				analyzeSignals(((RtlClockedBlock) block).getInitializerStatements());
			}
		}

	}

	private void analyzeSignals(RtlStatement statement) {
		statement.foreachSignalDependency(signal -> analyzeSignal(signal, VerilogExpressionNesting.ALL));
	}

	private void analyzeSignal(RtlSignal signal, VerilogExpressionNesting nesting) {

		// extract all signals that are used in more than one place. Those have been analyzed already.
		if (!allSignals.add(signal)) {
			declaredSignals.add(signal);
			return;
		}

		// also extract signals that do not comply with the current nesting level
		boolean compliesWithNesting = signal.compliesWith(nesting);
		if (!compliesWithNesting) {
			declaredSignals.add(signal);
		}

		// Analyze signals for sub-expressions by calling a "dry-run" printing process. While this sounds more complex,
		// it concentrates the complexity here, in one place, and simplifies the RtlSignal implementations.
		signal.printVerilogExpression(new VerilogExpressionWriter() {

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

		});

	}

	public enum VerilogExpressionNesting {
		ALL,
		SELECTIONS_SIGNALS_AND_CONSTANTS,
		SIGNALS_AND_CONSTANTS
	}

	private void printSignalDeclarations() {
		for (RtlSignal signal : declaredSignals) {
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
			out.printSignalName(signal);
		}
	}

}
