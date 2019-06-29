/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;

/**
 *
 */
public final class RtlOutputPin extends RtlPin {

	private RtlBitSignal outputSignal;

	public RtlOutputPin(RtlRealm realm) {
		super(realm);
	}

	public RtlBitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlBitSignal outputSignal) {
		this.outputSignal = checkSameRealm(outputSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(outputSignal, VerilogExpressionNesting.ALL);
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("output", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.print("assign " + getNetName() + " = ");
				out.printSignal(outputSignal);
				out.println(";");
			}

		};
	}

}
