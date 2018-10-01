/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 *
 */
public abstract class RtlConditionalOperation extends RtlItem implements RtlSignal {

	private final RtlBitSignal condition;

	public RtlConditionalOperation(RtlRealm realm, RtlBitSignal condition) {
		super(realm);
		this.condition = checkSameRealm(condition);
	}

	public final RtlBitSignal getCondition() {
		return condition;
	}

	public abstract RtlSignal getOnTrue();

	public abstract RtlSignal getOnFalse();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(condition, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" ? ");
		out.print(getOnTrue(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" : ");
		out.print(getOnFalse(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
