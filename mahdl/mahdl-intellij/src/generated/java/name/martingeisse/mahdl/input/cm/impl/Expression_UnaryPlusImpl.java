package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_UnaryPlus;
import org.jetbrains.annotations.NotNull;

public final class Expression_UnaryPlusImpl extends ExpressionImpl implements Expression_UnaryPlus, PsiCm {

	public Expression_UnaryPlusImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getOperand() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 1));
	}

	public ExpressionImpl getOperandPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 1);
	}

}
