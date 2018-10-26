/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;

/**
 * This pin type isn't supported by ISE generation but can be used to generate Verilog code for a module with
 * vector ports.
 */
public final class RtlVectorOutputPin extends RtlPin {

	private final int width;
	private RtlVectorSignal outputSignal;

	public RtlVectorOutputPin(RtlRealm realm, int width) {
		super(realm);
		this.width = width;
	}

	public RtlVectorSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlVectorSignal outputSignal) {
		if (outputSignal.getWidth() != width) {
			throw new IllegalArgumentException("trying to set signal with wrong width " + outputSignal.getWidth() + ", expected " + width);
		}
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
				consumer.consumePin("output", getNetName(), width);
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
