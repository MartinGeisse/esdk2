package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.StatementCaseItem;
import name.martingeisse.mahdl.input.cm.Statement_Switch;

public final class Statement_SwitchImpl extends StatementImpl implements Statement_Switch {

	private final Expression selector;
	private final CmList<StatementCaseItem> items;

	public Statement_SwitchImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.selector = (Expression) childNodes[2];
		((CmNodeImpl) this.selector).setParent(this);
		this.items = (CmList<StatementCaseItem>) childNodes[5];
		((CmNodeImpl) this.items).setParent(this);
	}

	public Expression getSelector() {
		return selector;
	}

	public CmList<StatementCaseItem> getItems() {
		return items;
	}

}
