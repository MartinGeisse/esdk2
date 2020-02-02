package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.DoBlockTrigger;
import name.martingeisse.mahdl.input.cm.ImplementationItem_DoBlock;
import name.martingeisse.mahdl.input.cm.Statement;

public final class ImplementationItem_DoBlockImpl extends ImplementationItemImpl implements ImplementationItem_DoBlock {

	private final DoBlockTrigger trigger;
	private final Statement statement;

	public ImplementationItem_DoBlockImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.trigger = (DoBlockTrigger) childNodes[2];
		((CmNodeImpl) this.trigger).setParent(this);
		this.statement = (Statement) childNodes[4];
		((CmNodeImpl) this.statement).setParent(this);
	}

	public DoBlockTrigger getTrigger() {
		return trigger;
	}

	public Statement getStatement() {
		return statement;
	}

}
