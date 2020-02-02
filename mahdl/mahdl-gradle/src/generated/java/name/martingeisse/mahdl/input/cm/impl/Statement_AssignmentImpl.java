package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import name.martingeisse.mahdl.input.cm.Statement_Assignment;

public final class Statement_AssignmentImpl extends StatementImpl implements Statement_Assignment {

	private final Expression leftSide;
	private final ExtendedExpression rightSide;

	public Statement_AssignmentImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.leftSide = (Expression) childNodes[0];
		((CmNodeImpl) this.leftSide).setParent(this);
		this.rightSide = (ExtendedExpression) childNodes[2];
		((CmNodeImpl) this.rightSide).setParent(this);
	}

	public Expression getLeftSide() {
		return leftSide;
	}

	public ExtendedExpression getRightSide() {
		return rightSide;
	}

}
