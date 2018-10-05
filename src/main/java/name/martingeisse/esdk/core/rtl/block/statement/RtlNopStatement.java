/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogWriter;

/**
 *
 */
public final class RtlNopStatement extends RtlStatement {

	public RtlNopStatement(RtlRealm realm) {
		super(realm);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
	}

}
