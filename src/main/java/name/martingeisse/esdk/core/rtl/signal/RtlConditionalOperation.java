/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public abstract class RtlConditionalOperation extends RtlItem implements RtlSignal {

	private final RtlBitSignal condition;

	public RtlConditionalOperation(RtlRealm realm, RtlBitSignal condition) {
		super(realm);
		this.condition = condition;
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
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(condition, VerilogGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" ? ");
		out.print(getOnTrue(), VerilogGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" : ");
		out.print(getOnFalse(), VerilogGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
