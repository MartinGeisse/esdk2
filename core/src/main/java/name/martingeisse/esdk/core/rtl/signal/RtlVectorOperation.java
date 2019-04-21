/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * See {@link RtlShiftOperation} for shifting. That class is separate because it has different width constraints on
 * the right operand.
 */
public final class RtlVectorOperation extends RtlItem implements RtlVectorSignal {

	private final Operator operator;
	private final RtlVectorSignal leftOperand;
	private final RtlVectorSignal rightOperand;

	public RtlVectorOperation(RtlRealm realm, Operator operator, RtlVectorSignal leftOperand, RtlVectorSignal rightOperand) {
		super(realm);
		if (leftOperand.getWidth() != rightOperand.getWidth()) {
			throw new IllegalArgumentException("operand width mismatch: " + leftOperand.getWidth() + " vs. " + rightOperand.getWidth());
		}
		this.operator = operator;
		this.leftOperand = checkSameRealm(leftOperand);
		this.rightOperand = checkSameRealm(rightOperand);
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
	public int getWidth() {
		return leftOperand.getWidth();
	}

	public enum Operator {
		ADD("+"),
		SUBTRACT("-"),
		MULTIPLY("*"),
		AND("&"),
		OR("|"),
		XOR("^");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}

		// ------------------------------------------------------------------------------------------------------------
		// simulation
		// ------------------------------------------------------------------------------------------------------------

		public VectorValue evaluate(VectorValue leftOperand, VectorValue rightOperand) {
			switch (this) {

				case ADD:
					return leftOperand.add(rightOperand);

				case SUBTRACT:
					return leftOperand.subtract(rightOperand);

				case MULTIPLY:
					return leftOperand.multiply(rightOperand);

				case AND:
					return leftOperand.and(rightOperand);

				case OR:
					return leftOperand.or(rightOperand);

				case XOR:
					return leftOperand.xor(rightOperand);

				default:
					throw new UnsupportedOperationException();

			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return operator.evaluate(leftOperand.getValue(), rightOperand.getValue());
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(leftOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(' ');
		out.print(operator.getSymbol());
		out.print(' ');
		out.print(rightOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
