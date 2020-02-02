package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import org.jetbrains.annotations.NotNull;

public abstract class ExtendedExpressionImpl extends ASTWrapperPsiElement implements ExtendedExpression, PsiCm {

	public ExtendedExpressionImpl(@NotNull ASTNode node) {
		super(node);
	}

}
