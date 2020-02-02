package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_RangeSelection;
import org.jetbrains.annotations.NotNull;

public final class Expression_RangeSelectionImpl extends ExpressionImpl implements Expression_RangeSelection, PsiCm {

	public Expression_RangeSelectionImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getContainer() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getContainerPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

	public Expression getFrom() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getFromPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

	public Expression getTo() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 4));
	}

	public ExpressionImpl getToPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 4);
	}

}
