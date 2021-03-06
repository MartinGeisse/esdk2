package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.ImplementationItem;
import org.jetbrains.annotations.NotNull;

public abstract class ImplementationItemImpl extends ASTWrapperPsiElement implements ImplementationItem, PsiCm {

	public ImplementationItemImpl(@NotNull ASTNode node) {
		super(node);
	}

}
