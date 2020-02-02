package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.Statement_Block;
import org.jetbrains.annotations.NotNull;

public final class Statement_BlockImpl extends StatementImpl implements Statement_Block, PsiCm {

	public Statement_BlockImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmList<Statement> getBody() {
		return (CmList<Statement>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public CmListImpl<Statement, StatementImpl> getBodyPsi() {
		return (CmListImpl<Statement, StatementImpl>) InternalPsiUtil.getChild(this, 1);
	}

}
