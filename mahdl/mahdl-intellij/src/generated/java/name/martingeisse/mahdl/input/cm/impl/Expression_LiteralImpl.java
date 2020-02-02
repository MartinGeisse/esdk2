package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression_Literal;
import name.martingeisse.mahdl.input.cm.Literal;
import org.jetbrains.annotations.NotNull;

public final class Expression_LiteralImpl extends ExpressionImpl implements Expression_Literal, PsiCm {

	public Expression_LiteralImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Literal getLiteral() {
		return (Literal) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public LiteralImpl getLiteralPsi() {
		return (LiteralImpl) InternalPsiUtil.getChild(this, 0);
	}

}
