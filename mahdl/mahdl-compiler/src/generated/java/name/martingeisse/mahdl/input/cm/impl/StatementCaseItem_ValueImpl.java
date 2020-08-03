package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.StatementCaseItem_Value;

public final class StatementCaseItem_ValueImpl extends StatementCaseItemImpl implements StatementCaseItem_Value {

	private final CmList<Expression> selectorValues;
	private final CmList<Statement> statements;

	public StatementCaseItem_ValueImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.selectorValues = (CmList<Expression>) childNodes[1];
		((CmNodeImpl) this.selectorValues).setParent(this);
		this.statements = (CmList<Statement>) childNodes[3];
		((CmNodeImpl) this.statements).setParent(this);
	}

	public CmList<Expression> getSelectorValues() {
		return selectorValues;
	}

	public CmList<Statement> getStatements() {
		return statements;
	}

}
