package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_UnaryMinus;

public final class Expression_UnaryMinusImpl extends ExpressionImpl implements Expression_UnaryMinus {

	private final Expression operand;

	public Expression_UnaryMinusImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.operand = (Expression) childNodes[1];
		((CmNodeImpl) this.operand).setParent(this);
	}

	public Expression getOperand() {
		return operand;
	}

}
