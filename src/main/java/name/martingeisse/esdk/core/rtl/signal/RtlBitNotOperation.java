/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitNotOperation extends RtlItem implements RtlBitSignal {

	private final RtlBitSignal operand;

	public RtlBitNotOperation(RtlDomain domain, RtlBitSignal operand) {
		super(domain);
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
