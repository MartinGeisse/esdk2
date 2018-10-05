/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.*;

import java.io.PrintWriter;

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
			public void analyzePins(PinConsumer consumer) {
				consumer.consumePin("output", getNetName());
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.print("assign " + getNetName() + " = ");
				out.print(outputSignal);
				out.println(";");
			}

		};
	}

}
