package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Statement;
import name.martingeisse.mahdl.input.cm.Statement_IfThen;
import org.jetbrains.annotations.NotNull;

public final class Statement_IfThenImpl extends StatementImpl implements Statement_IfThen, PsiCm {

	public Statement_IfThenImpl(@NotNull ASTNode node) {
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

}
