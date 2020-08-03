package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.ExpressionCaseItem_Default;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;

public final class ExpressionCaseItem_DefaultImpl extends ExpressionCaseItemImpl implements ExpressionCaseItem_Default {

	private final ExtendedExpression resultValue;

	public ExpressionCaseItem_DefaultImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.resultValue = (ExtendedExpression) childNodes[2];
		((CmNodeImpl) this.resultValue).setParent(this);
	}

	public ExtendedExpression getResultValue() {
		return resultValue;
	}

}
