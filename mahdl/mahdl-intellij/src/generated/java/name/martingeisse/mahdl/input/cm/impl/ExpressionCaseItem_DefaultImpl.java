package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.ExpressionCaseItem_Default;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import org.jetbrains.annotations.NotNull;

public final class ExpressionCaseItem_DefaultImpl extends ExpressionCaseItemImpl implements ExpressionCaseItem_Default, PsiCm {

	public ExpressionCaseItem_DefaultImpl(@NotNull ASTNode node) {
		super(node);
	}

	public ExtendedExpression getResultValue() {
		return (ExtendedExpression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExtendedExpressionImpl getResultValuePsi() {
		return (ExtendedExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

}
