package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExpressionCaseItem_Value;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import org.jetbrains.annotations.NotNull;

public final class ExpressionCaseItem_ValueImpl extends ExpressionCaseItemImpl implements ExpressionCaseItem_Value, PsiCm {

	public ExpressionCaseItem_ValueImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmList<Expression> getSelectorValues() {
		return (CmList<Expression>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public CmListImpl<Expression, ExpressionImpl> getSelectorValuesPsi() {
		return (CmListImpl<Expression, ExpressionImpl>) InternalPsiUtil.getChild(this, 1);
	}

	public ExtendedExpression getResultValue() {
		return (ExtendedExpression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 3));
	}

	public ExtendedExpressionImpl getResultValuePsi() {
		return (ExtendedExpressionImpl) InternalPsiUtil.getChild(this, 3);
	}

}
