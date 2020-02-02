package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Literal;
import org.jetbrains.annotations.NotNull;

public abstract class LiteralImpl extends ASTWrapperPsiElement implements Literal, PsiCm {

	public LiteralImpl(@NotNull ASTNode node) {
		super(node);
	}

}
