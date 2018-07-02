/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

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

	@Override
	public boolean getValue() {
		return operator.evaluate(leftOperand.getValue(), rightOperand.getValue());
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

		public boolean evaluate(boolean leftOperand, boolean rightOperand) {
			switch (this) {

				case AND:
					return leftOperand & rightOperand;

				case OR:
					return leftOperand | rightOperand;

				case XOR:
					return leftOperand ^ rightOperand;

				case XNOR:
					return leftOperand == rightOperand;

				default:
					throw new UnsupportedOperationException();

			}
		}

	}

}