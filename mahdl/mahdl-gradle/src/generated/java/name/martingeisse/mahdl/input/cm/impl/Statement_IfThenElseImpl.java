package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.Statement_IfThenElse;

public final class Statement_IfThenElseImpl extends StatementImpl implements Statement_IfThenElse {

	private final Expression condition;
	private final Statement thenBranch;
	private final Statement elseBranch;

	public Statement_IfThenElseImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.condition = (Expression) childNodes[2];
		((CmNodeImpl) this.condition).setParent(this);
		this.thenBranch = (Statement) childNodes[4];
		((CmNodeImpl) this.thenBranch).setParent(this);
		this.elseBranch = (Statement) childNodes[6];
		((CmNodeImpl) this.elseBranch).setParent(this);
	}

	public Expression getCondition() {
		return condition;
	}

	public Statement getThenBranch() {
		return thenBranch;
	}

	public Statement getElseBranch() {
		return elseBranch;
	}

}
