/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorComparison extends RtlItem implements RtlBitSignal {

	private final Operator operator;
	private final RtlVectorSignal leftOperand;
	private final RtlVectorSignal rightOperand;

	public RtlVectorComparison(RtlRealm realm, Operator operator, RtlVectorSignal leftOperand, RtlVectorSignal rightOperand) {
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

		// ------------------------------------------------------------------------------------------------------------
		// simulation
		// ------------------------------------------------------------------------------------------------------------

		public boolean evaluate(VectorValue leftOperand, VectorValue rightOperand) {
			switch (this) {

				case EQUAL:
					return leftOperand.equals(rightOperand);

				case NOT_EQUAL:
					return !leftOperand.equals(rightOperand);

				case UNSIGNED_LESS_THAN:

					return compare(leftOperand, rightOperand, true, false);

				case UNSIGNED_LESS_THAN_OR_EQUAL:
					return compare(leftOperand, rightOperand, true, true);

				case UNSIGNED_GREATER_THAN:
					return compare(leftOperand, rightOperand, false, false);

				case UNSIGNED_GREATER_THAN_OR_EQUAL:
					return compare(leftOperand, rightOperand, false, true);

				default:
					throw new UnsupportedOperationException();

			}
		}

		private boolean compare(VectorValue leftOperand, VectorValue rightOperand, boolean less, boolean equal) {
			int raw = leftOperand.compareUnsigned(rightOperand);
			return raw == 0 ? equal : (raw < 0) == less;
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
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
		out.printSignal(leftOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(' ');
		out.print(operator.getSymbol());
		out.print(' ');
		out.printSignal(rightOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
