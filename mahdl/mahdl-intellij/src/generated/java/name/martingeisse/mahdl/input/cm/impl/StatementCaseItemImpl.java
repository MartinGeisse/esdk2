package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.StatementCaseItem;
import org.jetbrains.annotations.NotNull;

public abstract class StatementCaseItemImpl extends ASTWrapperPsiElement implements StatementCaseItem, PsiCm {

	public StatementCaseItemImpl(@NotNull ASTNode node) {
		super(node);
	}

}
