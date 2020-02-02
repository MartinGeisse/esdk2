package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.Statement;

public abstract class StatementImpl extends CmNodeImpl implements Statement {

	public StatementImpl(int row, int column) {
		super(row, column);
	}

}
