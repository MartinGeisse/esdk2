package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExpressionCaseItem;
import name.martingeisse.mahdl.input.cm.ExtendedExpression_Switch;
import org.jetbrains.annotations.NotNull;

public final class ExtendedExpression_SwitchImpl extends ExtendedExpressionImpl implements ExtendedExpression_Switch, PsiCm {

	public ExtendedExpression_SwitchImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getSelector() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getSelectorPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

	public CmList<ExpressionCaseItem> getItems() {
		return (CmList<ExpressionCaseItem>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 5));
	}

	public CmListImpl<ExpressionCaseItem, ExpressionCaseItemImpl> getItemsPsi() {
		return (CmListImpl<ExpressionCaseItem, ExpressionCaseItemImpl>) InternalPsiUtil.getChild(this, 5);
	}

}
