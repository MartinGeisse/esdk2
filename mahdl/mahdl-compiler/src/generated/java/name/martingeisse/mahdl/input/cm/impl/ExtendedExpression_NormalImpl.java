package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExtendedExpression_Normal;

public final class ExtendedExpression_NormalImpl extends ExtendedExpressionImpl implements ExtendedExpression_Normal {

	private final Expression expression;

	public ExtendedExpression_NormalImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.expression = (Expression) childNodes[0];
		((CmNodeImpl) this.expression).setParent(this);
	}

	public Expression getExpression() {
		return expression;
	}

}
