package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_Parenthesized;
import org.jetbrains.annotations.NotNull;

public final class Expression_ParenthesizedImpl extends ExpressionImpl implements Expression_Parenthesized, PsiCm {

	public Expression_ParenthesizedImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getExpression() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public ExpressionImpl getExpressionPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 1);
	}

}
