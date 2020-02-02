package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_Conditional;
import org.jetbrains.annotations.NotNull;

public final class Expression_ConditionalImpl extends ExpressionImpl implements Expression_Conditional, PsiCm {

	public Expression_ConditionalImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getCondition() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getConditionPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

	public Expression getThenBranch() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getThenBranchPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

	public Expression getElseBranch() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 4));
	}

	public ExpressionImpl getElseBranchPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 4);
	}

}
