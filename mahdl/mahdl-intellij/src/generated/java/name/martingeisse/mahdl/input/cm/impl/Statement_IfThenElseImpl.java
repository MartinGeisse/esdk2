package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.Statement_IfThenElse;
import org.jetbrains.annotations.NotNull;

public final class Statement_IfThenElseImpl extends StatementImpl implements Statement_IfThenElse, PsiCm {

	public Statement_IfThenElseImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getCondition() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getConditionPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

	public Statement getThenBranch() {
		return (Statement) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 4));
	}

	public StatementImpl getThenBranchPsi() {
		return (StatementImpl) InternalPsiUtil.getChild(this, 4);
	}

	public Statement getElseBranch() {
		return (Statement) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 6));
	}

	public StatementImpl getElseBranchPsi() {
		return (StatementImpl) InternalPsiUtil.getChild(this, 6);
	}

}
