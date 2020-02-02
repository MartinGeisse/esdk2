package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DoBlockTrigger;
import org.jetbrains.annotations.NotNull;

public abstract class DoBlockTriggerImpl extends ASTWrapperPsiElement implements DoBlockTrigger, PsiCm {

	public DoBlockTriggerImpl(@NotNull ASTNode node) {
		super(node);
	}

}
