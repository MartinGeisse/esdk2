package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_UnaryPlus;

public final class Expression_UnaryPlusImpl extends ExpressionImpl implements Expression_UnaryPlus {

	private final Expression operand;

	public Expression_UnaryPlusImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.operand = (Expression) childNodes[1];
		((CmNodeImpl) this.operand).setParent(this);
	}

	public Expression getOperand() {
		return operand;
	}

}
