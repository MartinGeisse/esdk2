package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExtendedExpression_Normal;
import org.jetbrains.annotations.NotNull;

public final class ExtendedExpression_NormalImpl extends ExtendedExpressionImpl implements ExtendedExpression_Normal, PsiCm {

	public ExtendedExpression_NormalImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getExpression() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getExpressionPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

}
