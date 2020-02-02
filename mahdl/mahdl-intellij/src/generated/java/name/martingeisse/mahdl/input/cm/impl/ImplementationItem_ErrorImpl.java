package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.ImplementationItem_Error;
import org.jetbrains.annotations.NotNull;

public final class ImplementationItem_ErrorImpl extends ImplementationItemImpl implements ImplementationItem_Error, PsiCm {

	public ImplementationItem_ErrorImpl(@NotNull ASTNode node) {
		super(node);
	}

}
