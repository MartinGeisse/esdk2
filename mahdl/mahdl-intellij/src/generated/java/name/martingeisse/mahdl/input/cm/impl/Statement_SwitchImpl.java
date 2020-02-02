package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.StatementCaseItem;
import name.martingeisse.mahdl.input.cm.Statement_Switch;
import org.jetbrains.annotations.NotNull;

public final class Statement_SwitchImpl extends StatementImpl implements Statement_Switch, PsiCm {

	public Statement_SwitchImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getSelector() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getSelectorPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

	public CmList<StatementCaseItem> getItems() {
		return (CmList<StatementCaseItem>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 5));
	}

	public CmListImpl<StatementCaseItem, StatementCaseItemImpl> getItemsPsi() {
		return (CmListImpl<StatementCaseItem, StatementCaseItemImpl>) InternalPsiUtil.getChild(this, 5);
	}

}
