package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;

public abstract class ExpressionImpl extends CmNodeImpl implements Expression {

	public ExpressionImpl(int row, int column) {
		super(row, column);
	}

}
