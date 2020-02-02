package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_BinaryConcat;
import org.jetbrains.annotations.NotNull;

public final class Expression_BinaryConcatImpl extends ExpressionImpl implements Expression_BinaryConcat, PsiCm {

	public Expression_BinaryConcatImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getLeftOperand() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getLeftOperandPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

	public Expression getRightOperand() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getRightOperandPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

}
