/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisPreparationContext;
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
	public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
		fail();
	}

	@Override
	public void printDeclarations(VerilogWriter out) {
		fail();
	}

	@Override
	public void printImplementation(VerilogWriter out) {
		fail();
	}

	private void fail() {
		throw new SynthesisNotSupportedException("synthesis not supported for this design");
	}

}
