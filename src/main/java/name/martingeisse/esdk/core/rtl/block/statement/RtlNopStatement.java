/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

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
	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
	}

}
