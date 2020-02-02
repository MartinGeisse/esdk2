package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_IndexSelection;

public final class Expression_IndexSelectionImpl extends ExpressionImpl implements Expression_IndexSelection {

	private final Expression container;
	private final Expression index;

	public Expression_IndexSelectionImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.container = (Expression) childNodes[0];
		((CmNodeImpl) this.container).setParent(this);
		this.index = (Expression) childNodes[2];
		((CmNodeImpl) this.index).setParent(this);
	}

	public Expression getContainer() {
		return container;
	}

	public Expression getIndex() {
		return index;
	}

}
