package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DoBlockTrigger_Clocked;
import name.martingeisse.mahdl.input.cm.Expression;
import org.jetbrains.annotations.NotNull;

public final class DoBlockTrigger_ClockedImpl extends DoBlockTriggerImpl implements DoBlockTrigger_Clocked, PsiCm {

	public DoBlockTrigger_ClockedImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getClockExpression() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getClockExpressionPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

}
