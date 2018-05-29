/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.signal;

import name.martingeisse.esdk.rtl.*;
import name.martingeisse.esdk.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitNotOperation extends RtlItem implements RtlBitSignal {

	private final RtlBitSignal operand;

	public RtlBitNotOperation(RtlDesign design, RtlBitSignal operand) {
		super(design);
		this.operand = operand;
	}

	public RtlBitSignal getOperand() {
		return operand;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print('~');
		out.print(operand, VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
