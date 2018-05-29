/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.signal;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlItem;
import name.martingeisse.esdk.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlVectorComparison extends RtlItem implements RtlBitSignal {

	private final Operator operator;
	private final RtlVectorSignal leftOperand;
	private final RtlVectorSignal rightOperand;

	public RtlVectorComparison(RtlDesign design, Operator operator, RtlVectorSignal leftOperand, RtlVectorSignal rightOperand) {
		super(design);
		if (leftOperand.getWidth() != rightOperand.getWidth()) {
			throw new IllegalArgumentException("operand width mismatch: " + leftOperand.getWidth() + " vs. " + rightOperand.getWidth());
		}
		this.operator = operator;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public Operator getOperator() {
		return operator;
	}

	public RtlVectorSignal getLeftOperand() {
		return leftOperand;
	}

	public RtlVectorSignal getRightOperand() {
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
		EQUAL("=="),
		NOT_EQUAL("!="),
		UNSIGNED_LESS_THAN("<"),
		UNSIGNED_LESS_THAN_OR_EQUAL("<="),
		UNSIGNED_GREATER_THAN(">"),
		UNSIGNED_GREATER_THAN_OR_EQUAL(">=");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}

	}

}
