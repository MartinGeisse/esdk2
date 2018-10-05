/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public final class EmptyVerilogContribution implements VerilogContribution {

	@Override
	public void prepareSynthesis(SynthesisPreparationContext context) {
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
	}

	@Override
	public void printImplementation(VerilogWriter writer) {
	}

}
