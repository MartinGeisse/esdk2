/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

/**
 *
 */
public interface VerilogContribution {

	void prepareSynthesis(SynthesisPreparationContext context);

	void analyzeSignalUsage(SignalUsageConsumer consumer);

	default void analyzePins(PinConsumer consumer) {
	}

	default void printDeclarations(VerilogWriter out) {
	}

	void printImplementation(VerilogWriter out);

}
