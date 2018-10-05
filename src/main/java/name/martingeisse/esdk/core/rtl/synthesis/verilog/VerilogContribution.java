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

	void analyzePins(PinConsumer consumer);

	void printImplementation(VerilogWriter out);

}
