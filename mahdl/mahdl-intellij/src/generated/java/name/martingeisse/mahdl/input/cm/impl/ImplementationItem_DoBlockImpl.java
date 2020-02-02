package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DoBlockTrigger;
import name.martingeisse.mahdl.input.cm.ImplementationItem_DoBlock;
import name.martingeisse.mahdl.input.cm.Statement;
import org.jetbrains.annotations.NotNull;

public final class ImplementationItem_DoBlockImpl extends ImplementationItemImpl implements ImplementationItem_DoBlock, PsiCm {

	public ImplementationItem_DoBlockImpl(@NotNull ASTNode node) {
		super(node);
	}

	public DoBlockTrigger getTrigger() {
		return (DoBlockTrigger) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public DoBlockTriggerImpl getTriggerPsi() {
		return (DoBlockTriggerImpl) InternalPsiUtil.getChild(this, 2);
	}

	public Statement getStatement() {
		return (Statement) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 4));
	}

	public StatementImpl getStatementPsi() {
		return (StatementImpl) InternalPsiUtil.getChild(this, 4);
	}

}
