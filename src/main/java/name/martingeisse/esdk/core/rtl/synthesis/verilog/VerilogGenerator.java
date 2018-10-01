/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class VerilogGenerator {

	private final VerilogWriter out;
	private final RtlRealm realm;
	private final String name;

	private SignalAnalyzer signalAnalyzer;

	public VerilogGenerator(PrintWriter out, RtlRealm realm, String name) {
		this.out = new VerilogWriter(out);
		this.realm = realm;
		this.name = name;
	}

	public void generate() {
		signalAnalyzer = new SignalAnalyzer(realm);
		out.prepare(new HashMap<>(), signalAnalyzer.getNamedSignals());
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

	private void printSignalDeclarations() {
		for (Map.Entry<RtlSignal, String> signalEntry : signalAnalyzer.getDeclaredSignals().entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			String signalName = signalEntry.getValue();
			if (signal instanceof RtlProceduralSignal) {
				out.getOut().print("reg");
			} else {
				out.getOut().print("wire");
			}
			if (signal instanceof RtlVectorSignal) {
				RtlVectorSignal vectorSignal = (RtlVectorSignal) signal;
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
		for (Map.Entry<RtlSignal, String> signalEntry : signalAnalyzer.getNamedSignals().entrySet()) {
			RtlSignal signal = signalEntry.getKey();
			String signalName = signalEntry.getValue();
			if (signal instanceof RtlPin || signal instanceof RtlProceduralSignal) {
				continue;
			}
			out.getOut().print("assign " + signalName + " = ");
			out.printImplementationExpression(signal);
			out.getOut().println(";");
		}
	}

	private void printBlocks() {
		for (RtlClockedItem item : realm.getClockedItems()) {
			item.printImplementation(out);
		}
	}

	private void printOutputPinAssignments() {
		for (RtlPin pin : realm.getPins()) {
			if (pin instanceof RtlOutputPin) {
				out.getOut().print("assign " + pin.getNetName() + " = ");
				out.printExpression(((RtlOutputPin) pin).getOutputSignal());
				out.getOut().println(";");
			} else if (pin instanceof RtlBidirectionalPin) {
				RtlBidirectionalPin bidirectionalPin = (RtlBidirectionalPin) pin;
				out.getOut().print("assign " + pin.getNetName() + " = ");
				out.printExpression(bidirectionalPin.getOutputEnableSignal());
				out.getOut().println(" ? ");
				out.printExpression(bidirectionalPin.getOutputSignal());
				out.getOut().println(" : 1'bz;");
			}
		}
	}

}
