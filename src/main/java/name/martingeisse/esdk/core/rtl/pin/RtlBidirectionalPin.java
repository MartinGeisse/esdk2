/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.*;

/**
 * TODO generate tri-state assignment
 */
public final class RtlBidirectionalPin extends RtlPin implements RtlBitSignal {

	private final RtlSettableBitSignal settableInputBitSignal;
	private RtlBitSignal outputSignal;
	private RtlBitSignal outputEnableSignal;

	public RtlBidirectionalPin(RtlRealm realm) {
		super(realm);
		this.settableInputBitSignal = new RtlSettableBitSignal(realm);
	}

	public RtlSettableBitSignal getSettableInputBitSignal() {
		return settableInputBitSignal;
	}

	public RtlBitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlBitSignal outputSignal) {
		this.outputSignal = checkSameRealm(outputSignal);
	}

	public RtlBitSignal getOutputEnableSignal() {
		return outputEnableSignal;
	}

	public void setOutputEnableSignal(RtlBitSignal outputEnableSignal) {
		this.outputEnableSignal = checkSameRealm(outputEnableSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return outputEnableSignal.getValue() ? outputSignal.getValue() : settableInputBitSignal.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.declareSignal(RtlBidirectionalPin.this, getNetName(), false, null, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(outputSignal, VerilogExpressionNesting.ALL);
				consumer.consumeSignalUsage(outputEnableSignal, VerilogExpressionNesting.ALL);
			}

			@Override
			public void analyzePins(PinConsumer consumer) {
				consumer.consumePin("inout", getNetName());
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.print("assign " + getNetName() + " = ");
				out.print(outputEnableSignal);
				out.println(" ? ");
				out.print(outputSignal);
				out.println(" : 1'bz;");
			}

		};
	}

}
