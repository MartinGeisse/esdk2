package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.Statement_IfThen;

public final class Statement_IfThenImpl extends StatementImpl implements Statement_IfThen {

	private final Expression condition;
	private final Statement thenBranch;

	public Statement_IfThenImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.condition = (Expression) childNodes[2];
		((CmNodeImpl) this.condition).setParent(this);
		this.thenBranch = (Statement) childNodes[4];
		((CmNodeImpl) this.thenBranch).setParent(this);
	}

	public Expression getCondition() {
		return condition;
	}

	public Statement getThenBranch() {
		return thenBranch;
	}

}
