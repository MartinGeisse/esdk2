package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.StatementCaseItem_Default;
import org.jetbrains.annotations.NotNull;

public final class StatementCaseItem_DefaultImpl extends StatementCaseItemImpl implements StatementCaseItem_Default, PsiCm {

	public StatementCaseItem_DefaultImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmList<Statement> getStatements() {
		return (CmList<Statement>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public CmListImpl<Statement, StatementImpl> getStatementsPsi() {
		return (CmListImpl<Statement, StatementImpl>) InternalPsiUtil.getChild(this, 2);
	}

}
