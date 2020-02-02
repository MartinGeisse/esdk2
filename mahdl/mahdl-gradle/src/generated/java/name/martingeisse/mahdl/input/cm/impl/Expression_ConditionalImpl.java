package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_Conditional;

public final class Expression_ConditionalImpl extends ExpressionImpl implements Expression_Conditional {

	private final Expression condition;
	private final Expression thenBranch;
	private final Expression elseBranch;

	public Expression_ConditionalImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.condition = (Expression) childNodes[0];
		((CmNodeImpl) this.condition).setParent(this);
		this.thenBranch = (Expression) childNodes[2];
		((CmNodeImpl) this.thenBranch).setParent(this);
		this.elseBranch = (Expression) childNodes[4];
		((CmNodeImpl) this.elseBranch).setParent(this);
	}

	public Expression getCondition() {
		return condition;
	}

	public Expression getThenBranch() {
		return thenBranch;
	}

	public Expression getElseBranch() {
		return elseBranch;
	}

}
