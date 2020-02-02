package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import name.martingeisse.mahdl.input.cm.SignalLikeDefinition;
import org.jetbrains.annotations.NotNull;

public abstract class SignalLikeDefinitionImpl extends ASTWrapperPsiElement implements SignalLikeDefinition, PsiCm, PsiNameIdentifierOwner {

	public SignalLikeDefinitionImpl(@NotNull ASTNode node) {
		super(node);
	}

	public void superclassDelete() throws IncorrectOperationException {
		super.delete();
	}

}
