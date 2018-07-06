/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlRegion;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitNotOperation extends RtlItem implements RtlBitSignal {

	private final RtlBitSignal operand;

	public RtlBitNotOperation(RtlRegion region, RtlBitSignal operand) {
		super(region);
		this.operand = operand;
	}

	public RtlBitSignal getOperand() {
		return operand;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print('~');
		out.print(operand, VerilogGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

	@Override
	public boolean getValue() {
		return !operand.getValue();
	}

}
