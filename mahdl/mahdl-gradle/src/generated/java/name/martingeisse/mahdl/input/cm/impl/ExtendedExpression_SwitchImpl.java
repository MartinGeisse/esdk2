package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExpressionCaseItem;
import name.martingeisse.mahdl.input.cm.ExtendedExpression_Switch;

public final class ExtendedExpression_SwitchImpl extends ExtendedExpressionImpl implements ExtendedExpression_Switch {

	private final Expression selector;
	private final CmList<ExpressionCaseItem> items;

	public ExtendedExpression_SwitchImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.selector = (Expression) childNodes[2];
		((CmNodeImpl) this.selector).setParent(this);
		this.items = (CmList<ExpressionCaseItem>) childNodes[5];
		((CmNodeImpl) this.items).setParent(this);
	}

	public Expression getSelector() {
		return selector;
	}

	public CmList<ExpressionCaseItem> getItems() {
		return items;
	}

}
