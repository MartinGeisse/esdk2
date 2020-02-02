package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.SignalLikeKind;
import org.jetbrains.annotations.NotNull;

public abstract class SignalLikeKindImpl extends ASTWrapperPsiElement implements SignalLikeKind, PsiCm {

	public SignalLikeKindImpl(@NotNull ASTNode node) {
		super(node);
	}

}
