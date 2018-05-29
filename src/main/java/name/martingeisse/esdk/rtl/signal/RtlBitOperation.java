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
public final class RtlBitOperation extends RtlItem implements RtlBitSignal {

	private final Operator operator;
	private final RtlBitSignal leftOperand;
	private final RtlBitSignal rightOperand;

	public RtlBitOperation(RtlDesign design, Operator operator, RtlBitSignal leftOperand, RtlBitSignal rightOperand) {
		super(design);
		this.operator = operator;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public Operator getOperator() {
		return operator;
	}

	public RtlBitSignal getLeftOperand() {
		return leftOperand;
	}

	public RtlBitSignal getRightOperand() {
		return rightOperand;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(leftOperand, VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(' ');
		out.print(operator.getSymbol());
		out.print(' ');
		out.print(rightOperand, VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

	public enum Operator {
		AND("&"),
		OR("|"),
		XOR("^"),
		XNOR("==");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}

	}

}
