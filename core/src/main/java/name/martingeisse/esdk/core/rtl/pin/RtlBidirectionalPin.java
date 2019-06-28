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
public final class RtlBidirectionalPin extends RtlPin implements RtlBitSignal {

	private final PinSimulationSignal settableInputBitSignal;
	private RtlBitSignal outputSignal;
	private RtlBitSignal outputEnableSignal;

	public RtlBidirectionalPin(RtlRealm realm) {
		super(realm);
		this.settableInputBitSignal = new PinSimulationSignal(realm);
	}

	public PinSimulationSignal getSettableInputBitSignal() {
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
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// We analyze the output and output-enable signals in a separate VerilogContribution.
		// This is correct; we cannot analyze them here because those usages should be analyzed
		// regardless of whether the pin's input signal is used anywhere.
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
				context.declareFixedNameSignal(RtlBidirectionalPin.this, getNetName(), null, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(outputSignal, VerilogExpressionNesting.ALL);
				consumer.consumeSignalUsage(outputEnableSignal, VerilogExpressionNesting.ALL);
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("inout", getNetName(), null);
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
