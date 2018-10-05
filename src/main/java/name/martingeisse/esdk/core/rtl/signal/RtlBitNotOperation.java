/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitNotOperation extends RtlItem implements RtlBitSignal {

	private final RtlBitSignal operand;

	public RtlBitNotOperation(RtlRealm realm, RtlBitSignal operand) {
		super(realm);
		this.operand = checkSameRealm(operand);
	}

	public RtlBitSignal getOperand() {
		return operand;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return !operand.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print('~');
		out.print(operand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
