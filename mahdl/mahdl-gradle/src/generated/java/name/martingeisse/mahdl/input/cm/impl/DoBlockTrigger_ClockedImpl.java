package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.DoBlockTrigger_Clocked;
import name.martingeisse.mahdl.input.cm.Expression;

public final class DoBlockTrigger_ClockedImpl extends DoBlockTriggerImpl implements DoBlockTrigger_Clocked {

	private final Expression clockExpression;

	public DoBlockTrigger_ClockedImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.clockExpression = (Expression) childNodes[0];
		((CmNodeImpl) this.clockExpression).setParent(this);
	}

	public Expression getClockExpression() {
		return clockExpression;
	}

}
