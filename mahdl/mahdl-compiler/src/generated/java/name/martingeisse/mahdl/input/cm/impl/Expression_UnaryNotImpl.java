package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_UnaryNot;

public final class Expression_UnaryNotImpl extends ExpressionImpl implements Expression_UnaryNot {

	private final Expression operand;

	public Expression_UnaryNotImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.operand = (Expression) childNodes[1];
		((CmNodeImpl) this.operand).setParent(this);
	}

	public Expression getOperand() {
		return operand;
	}

}
