package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.Expression_Identifier;
import org.jetbrains.annotations.NotNull;

public final class Expression_IdentifierImpl extends ExpressionImpl implements Expression_Identifier, PsiCm {

	public Expression_IdentifierImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmToken getIdentifier() {
		return (CmToken) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public LeafPsiElement getIdentifierPsi() {
		return (LeafPsiElement) InternalPsiUtil.getChild(this, 0);
	}

	public PsiReference getReference() {
		return name.martingeisse.mahdl.intellij.input.PsiUtil.getReference(this);
	}

}
