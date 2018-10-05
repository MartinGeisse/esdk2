/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlBitSignal {

	private final RtlSettableBitSignal settableBitSignal;

	public RtlInputPin(RtlRealm realm) {
		super(realm);
		this.settableBitSignal = new RtlSettableBitSignal(realm);
	}

	public RtlSettableBitSignal getSettableBitSignal() {
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
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException();
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.declareSignal(RtlInputPin.this, getNetName(), false, null, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzePins(PinConsumer consumer) {
				consumer.consumePin("input", getNetName());
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
