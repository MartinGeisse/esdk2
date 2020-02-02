package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.PortDefinitionGroup;
import org.jetbrains.annotations.NotNull;

public abstract class PortDefinitionGroupImpl extends ASTWrapperPsiElement implements PortDefinitionGroup, PsiCm {

	public PortDefinitionGroupImpl(@NotNull ASTNode node) {
		super(node);
	}

}
