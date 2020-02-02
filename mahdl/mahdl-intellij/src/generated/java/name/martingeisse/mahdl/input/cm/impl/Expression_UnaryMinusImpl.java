package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_UnaryMinus;
import org.jetbrains.annotations.NotNull;

public final class Expression_UnaryMinusImpl extends ExpressionImpl implements Expression_UnaryMinus, PsiCm {

	public Expression_UnaryMinusImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getOperand() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public ExpressionImpl getOperandPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 1);
	}

}
