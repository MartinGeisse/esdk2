package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.ExpressionCaseItem;
import org.jetbrains.annotations.NotNull;

public abstract class ExpressionCaseItemImpl extends ASTWrapperPsiElement implements ExpressionCaseItem, PsiCm {

	public ExpressionCaseItemImpl(@NotNull ASTNode node) {
		super(node);
	}

}
