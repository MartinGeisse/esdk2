/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitOperation extends RtlItem implements RtlBitSignal {

	private final Operator operator;
	private final RtlBitSignal leftOperand;
	private final RtlBitSignal rightOperand;

	public RtlBitOperation(RtlRealm realm, Operator operator, RtlBitSignal leftOperand, RtlBitSignal rightOperand) {
		super(realm);
		this.operator = operator;
		this.leftOperand = checkSameRealm(leftOperand);
		this.rightOperand = checkSameRealm(rightOperand);
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

		// ------------------------------------------------------------------------------------------------------------
		// simulation
		// ------------------------------------------------------------------------------------------------------------

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
		out.print(leftOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(' ');
		out.print(operator.getSymbol());
		out.print(' ');
		out.print(rightOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
