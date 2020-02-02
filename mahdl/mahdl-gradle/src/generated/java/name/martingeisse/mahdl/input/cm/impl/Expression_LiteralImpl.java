package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression_Literal;
import name.martingeisse.mahdl.input.cm.Literal;

public final class Expression_LiteralImpl extends ExpressionImpl implements Expression_Literal {

	private final Literal literal;

	public Expression_LiteralImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.literal = (Literal) childNodes[0];
		((CmNodeImpl) this.literal).setParent(this);
	}

	public Literal getLiteral() {
		return literal;
	}

}
