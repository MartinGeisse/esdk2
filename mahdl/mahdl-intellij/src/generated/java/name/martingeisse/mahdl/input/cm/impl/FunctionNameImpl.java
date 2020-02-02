package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.FunctionName;
import org.jetbrains.annotations.NotNull;

public abstract class FunctionNameImpl extends ASTWrapperPsiElement implements FunctionName, PsiCm {

	public FunctionNameImpl(@NotNull ASTNode node) {
		super(node);
	}

}
