package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.Statement_Block;

public final class Statement_BlockImpl extends StatementImpl implements Statement_Block {

	private final CmList<Statement> body;

	public Statement_BlockImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.body = (CmList<Statement>) childNodes[1];
		((CmNodeImpl) this.body).setParent(this);
	}

	public CmList<Statement> getBody() {
		return body;
	}

}
