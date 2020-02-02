package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_Parenthesized;

public final class Expression_ParenthesizedImpl extends ExpressionImpl implements Expression_Parenthesized {

	private final Expression expression;

	public Expression_ParenthesizedImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.expression = (Expression) childNodes[1];
		((CmNodeImpl) this.expression).setParent(this);
	}

	public Expression getExpression() {
		return expression;
	}

}
