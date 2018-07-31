/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.verilog;

import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

import java.io.PrintWriter;
import java.util.*;

/**
 *
 */
public class VerilogGenerator {

	private final VerilogWriter out;
	private final RtlRealm realm;
	private final String name;

	private List<RtlClockedBlock> clockedBlocks;
	private Set<RtlSignal> allSignals;
	private Map<RtlSignal, String> declaredSignals;
	private VerilogExpressionWriter dryRunExpressionWriter;


	public VerilogGenerator(PrintWriter out, RtlRealm realm, String name) {
		this.out = new VerilogWriter(out);
		this.realm = realm;
		this.name = name;
	}

	public void generate() {
		copyBlocks();
		analyzeSignals();
		out.prepare(new HashMap<>(), declaredSignals);
		out.printIntro(name, realm.getPins());
		printSignalDeclarations();
		out.getOut().println();
		printSignalAssignments();
		out.getOut().println();
		printBlocks();
		out.getOut().println();
		printOutputPinAssignments();
		out.getOut().println();
		out.printOutro();
	}

	//
	// preparation
	//

	private void copyBlocks() {
		clockedBlocks = new ArrayList<>();
		for (RtlClockedItem item : realm.getClockedItems()) {
			if (item instanceof RtlClockedBlock) {
				clockedBlocks.add((RtlClockedBlock)item);
			} else {
				throw new RuntimeException("cannot synthesize RtlClockedItems except RtlClockedBlock");
			}
		}

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

		// pins are implicitly declared
		for (RtlPin pin : realm.getPins()) {
			if (pin instanceof RtlInputPin) {
				declaredSignals.put((RtlInputPin)pin, pin.getNetName());
			}
		}

		// procedural signals must be declared
		for (RtlClockedBlock block : clockedBlocks) {
			for (RtlProceduralSignal signal : block.getProceduralSignals()) {
				allSignals.add(signal);
				declareSignal(signal);
			}
		}

		// analyze output and output-enable signals for signals to extract
		for (RtlPin pin : realm.getPins()) {
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
		for (RtlClockedBlock block : clockedBlocks) {
			block.getInitializerStatements().printExpressionsDryRun(dryRunExpressionWriter);
			block.getStatements().printExpressionsDryRun(dryRunExpressionWriter);
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

	private void printSignalAssignments() {
		for (Map.Entry<RtlSignal, String> signalEntry : declaredSignals.entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			String signalName = signalEntry.getValue();
			if (signal instanceof RtlPin || signal instanceof RtlProceduralSignal) {
				continue;
			}
			out.getOut().print("assign " + signalName + " = ");
			out.printDefiningExpression(signal);
			out.getOut().println(";");
		}
	}

	private void printBlocks() {
		for (RtlClockedBlock block : clockedBlocks) {
			block.printVerilogBlocks(out);
		}
	}

	private void printOutputPinAssignments() {
		for (RtlPin pin : realm.getPins()) {
			if (pin instanceof RtlOutputPin) {
				out.getOut().print("assign " + pin.getNetName() + " = ");
				out.printExpression(((RtlOutputPin) pin).getOutputSignal());
				out.getOut().println(";");
			} else if (pin instanceof RtlBidirectionalPin) {
				RtlBidirectionalPin bidirectionalPin = (RtlBidirectionalPin)pin;
				out.getOut().print("assign " + pin.getNetName() + " = ");
				out.printExpression(bidirectionalPin.getOutputEnableSignal());
				out.getOut().println(" ? ");
				out.printExpression(bidirectionalPin.getOutputSignal());
				out.getOut().println(" : 1'bz;");
			}
		}
	}

}
