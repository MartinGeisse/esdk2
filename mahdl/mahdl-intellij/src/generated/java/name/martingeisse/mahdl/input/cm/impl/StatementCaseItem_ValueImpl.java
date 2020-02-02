package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.StatementCaseItem_Value;
import org.jetbrains.annotations.NotNull;

public final class StatementCaseItem_ValueImpl extends StatementCaseItemImpl implements StatementCaseItem_Value, PsiCm {

	public StatementCaseItem_ValueImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmList<Expression> getSelectorValues() {
		return (CmList<Expression>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public CmListImpl<Expression, ExpressionImpl> getSelectorValuesPsi() {
		return (CmListImpl<Expression, ExpressionImpl>) InternalPsiUtil.getChild(this, 1);
	}

	public CmList<Statement> getStatements() {
		return (CmList<Statement>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 3));
	}

	public CmListImpl<Statement, StatementImpl> getStatementsPsi() {
		return (CmListImpl<Statement, StatementImpl>) InternalPsiUtil.getChild(this, 3);
	}

}
