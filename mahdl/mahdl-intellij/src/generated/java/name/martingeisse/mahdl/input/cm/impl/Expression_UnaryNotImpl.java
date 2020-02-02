package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_UnaryNot;
import org.jetbrains.annotations.NotNull;

public final class Expression_UnaryNotImpl extends ExpressionImpl implements Expression_UnaryNot, PsiCm {

	public Expression_UnaryNotImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getOperand() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public ExpressionImpl getOperandPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 1);
	}

}
