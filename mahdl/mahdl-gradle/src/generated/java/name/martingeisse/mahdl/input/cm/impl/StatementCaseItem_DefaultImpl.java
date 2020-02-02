package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.StatementCaseItem_Default;

public final class StatementCaseItem_DefaultImpl extends StatementCaseItemImpl implements StatementCaseItem_Default {

	private final CmList<Statement> statements;

	public StatementCaseItem_DefaultImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.statements = (CmList<Statement>) childNodes[2];
		((CmNodeImpl) this.statements).setParent(this);
	}

	public CmList<Statement> getStatements() {
		return statements;
	}

}
