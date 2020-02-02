package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.Literal_Text;
import org.jetbrains.annotations.NotNull;

public final class Literal_TextImpl extends LiteralImpl implements Literal_Text, PsiCm {

	public Literal_TextImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmToken getValue() {
		return (CmToken) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public LeafPsiElement getValuePsi() {
		return (LeafPsiElement) InternalPsiUtil.getChild(this, 0);
	}

}
