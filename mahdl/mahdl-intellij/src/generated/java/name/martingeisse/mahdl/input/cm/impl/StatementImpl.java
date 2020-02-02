package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Statement;
import org.jetbrains.annotations.NotNull;

public abstract class StatementImpl extends ASTWrapperPsiElement implements Statement, PsiCm {

	public StatementImpl(@NotNull ASTNode node) {
		super(node);
	}

}
