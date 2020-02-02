package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import name.martingeisse.mahdl.input.cm.CmList;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.QualifiedModuleName;
import org.jetbrains.annotations.NotNull;

public final class QualifiedModuleNameImpl extends ASTWrapperPsiElement implements QualifiedModuleName, PsiCm {

	public QualifiedModuleNameImpl(@NotNull ASTNode node) {
		super(node);
	}

	public CmList<CmToken> getSegments() {
		return (CmList<CmToken>) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 0));
	}

	public CmListImpl<CmToken, LeafPsiElement> getSegmentsPsi() {
		return (CmListImpl<CmToken, LeafPsiElement>) InternalPsiUtil.getChild(this, 0);
	}

	public PsiReference getReference() {
		return name.martingeisse.mahdl.intellij.input.PsiUtil.getReference(this);
	}

}
