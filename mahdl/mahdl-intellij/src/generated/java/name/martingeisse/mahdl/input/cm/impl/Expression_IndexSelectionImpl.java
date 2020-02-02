package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.Expression_IndexSelection;
import org.jetbrains.annotations.NotNull;

public final class Expression_IndexSelectionImpl extends ExpressionImpl implements Expression_IndexSelection, PsiCm {

	public Expression_IndexSelectionImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getContainer() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public ExpressionImpl getContainerPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 0);
	}

	public Expression getIndex() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getIndexPsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

}
