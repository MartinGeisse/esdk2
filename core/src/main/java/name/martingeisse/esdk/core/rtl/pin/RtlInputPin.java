/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlBitSignal {

	private final PinSimulationSignal settableBitSignal;

	public RtlInputPin(RtlRealm realm) {
		super(realm);
		this.settableBitSignal = new PinSimulationSignal(realm);
	}

	public PinSimulationSignal getSettableBitSignal() {
		return settableBitSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return settableBitSignal.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// input pins don't use any other signals
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException();
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.declareFixedNameSignal(RtlInputPin.this, getNetName(), null, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzePins(PinConsumer consumer) {
				consumer.consumePin("input", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
