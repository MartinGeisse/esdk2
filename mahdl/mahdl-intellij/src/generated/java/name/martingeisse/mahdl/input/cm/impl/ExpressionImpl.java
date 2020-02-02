package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Expression;
import org.jetbrains.annotations.NotNull;

public abstract class ExpressionImpl extends ASTWrapperPsiElement implements Expression, PsiCm {

	public ExpressionImpl(@NotNull ASTNode node) {
		super(node);
	}

}
