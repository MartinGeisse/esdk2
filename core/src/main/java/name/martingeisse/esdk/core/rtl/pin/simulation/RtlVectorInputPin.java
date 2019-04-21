/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * This pin type isn't supported by ISE generation but can be used to generate Verilog code for a module with
 * vector ports.
 */
public final class RtlVectorInputPin extends RtlPin implements RtlVectorSignal {

	private final int width;
	private final VectorPinSimulationSignal simulationSignal;

	public RtlVectorInputPin(RtlRealm realm, int width) {
		super(realm);
		this.simulationSignal = new VectorPinSimulationSignal(realm, width);
		this.width = width;
	}

	public VectorPinSimulationSignal getSimulationSignal() {
		return simulationSignal;
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return simulationSignal.getValue();
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
				context.declareSignal(RtlVectorInputPin.this, getNetName(), false, null, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzePins(PinConsumer consumer) {
				consumer.consumePin("input", getNetName(), width);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
