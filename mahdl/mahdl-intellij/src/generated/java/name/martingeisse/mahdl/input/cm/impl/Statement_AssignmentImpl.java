package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import name.martingeisse.mahdl.input.cm.Statement_Assignment;
import org.jetbrains.annotations.NotNull;

public final class Statement_AssignmentImpl extends StatementImpl implements Statement_Assignment, PsiCm {

	public Statement_AssignmentImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getLeftSide() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getLeftSidePsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

	public ExtendedExpression getRightSide() {
		return (ExtendedExpression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExtendedExpressionImpl getRightSidePsi() {
		return (ExtendedExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

}
