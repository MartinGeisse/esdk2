/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.module.RtlInstanceOutputPort;
import name.martingeisse.esdk.core.rtl.module.RtlInstancePort;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionNesting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
final class SignalAnalyzer {

	private final RtlRealm realm;
	private final Set<RtlSignal> analyzedSignals;
	private final Map<RtlSignal, String> namedSignals;
	private final Map<RtlSignal, String> declaredSignals;

	SignalAnalyzer(RtlRealm realm) {
		this.realm = realm;
		this.analyzedSignals = new HashSet<>();
		this.namedSignals = new HashMap<>();
		this.declaredSignals = new HashMap<>();
	}

	public Map<RtlSignal, String> getNamedSignals() {
		return namedSignals;
	}

	public Map<RtlSignal, String> getDeclaredSignals() {
		return declaredSignals;
	}

	void analyze() {

		// input pins and bidirectional pins are implicitly known via the toplevel port list
		for (RtlPin pin : realm.getPins()) {
			if (pin instanceof RtlInputPin) {
				RtlInputPin inputPin = (RtlInputPin) pin;
				analyzedSignals.add(inputPin);
				namedSignals.put(inputPin, inputPin.getNetName());
			} else if (pin instanceof RtlBidirectionalPin) {
				RtlBidirectionalPin bidirectionalPin = (RtlBidirectionalPin) pin;
				analyzedSignals.add(bidirectionalPin);
				namedSignals.put(bidirectionalPin, bidirectionalPin.getNetName());
			}
		}

		// procedural signals and similar signals must be declared
		for (RtlClockedItem item : realm.getClockedItems()) {
			for (RtlSignal signal : item.getSignalsThatRequireDeclarationInVerilog()) {
				analyzedSignals.add(signal);
				declareSignal(signal);
			}
		}

		// module output signals must be declared
		for (RtlModuleInstance moduleInstance : realm.getModuleInstances()) {
			for (RtlInstancePort port : moduleInstance.getPorts()) {
				if (port instanceof RtlInstanceOutputPort) {
					RtlInstanceOutputPort outputPort = (RtlInstanceOutputPort) port;
					analyzedSignals.add(outputPort);
					declareSignal(outputPort);
				}
			}
		}

		// analyze output and output-enable signals for signals to extract
		for (RtlPin pin : realm.getPins()) {
			if (pin instanceof RtlOutputPin) {
				RtlOutputPin outputPin = (RtlOutputPin) pin;
				analyzeSignal(outputPin.getOutputSignal(), VerilogExpressionNesting.ALL);
			} else if (pin instanceof RtlBidirectionalPin) {
				RtlBidirectionalPin bidirectionalPin = (RtlBidirectionalPin) pin;
				analyzeSignal(bidirectionalPin.getOutputSignal(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
				analyzeSignal(bidirectionalPin.getOutputEnableSignal(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
			}
		}

		// analyze signal "expressions" in blocks for signals to extract
		for (RtlClockedItem item : realm.getClockedItems()) {
			item.printExpressionsDryRun(dryRunExpressionWriter);
		}

	}

}
