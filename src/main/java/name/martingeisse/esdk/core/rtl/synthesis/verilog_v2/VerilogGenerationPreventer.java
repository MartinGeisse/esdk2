/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisNotSupportedException;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public final class VerilogGenerationPreventer implements VerilogContribution {

	@Override
	public void prepareSynthesis(SynthesisPreparationContext context) {
		fail();
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		fail();
	}

	@Override
	public void printImplementation(VerilogWriter writer) {
		fail();
	}

	private void fail() {
		throw new SynthesisNotSupportedException();
	}

}
