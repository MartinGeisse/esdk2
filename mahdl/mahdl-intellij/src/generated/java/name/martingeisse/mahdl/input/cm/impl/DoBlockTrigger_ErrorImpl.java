package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DoBlockTrigger_Error;
import org.jetbrains.annotations.NotNull;

public final class DoBlockTrigger_ErrorImpl extends DoBlockTriggerImpl implements DoBlockTrigger_Error, PsiCm {

	public DoBlockTrigger_ErrorImpl(@NotNull ASTNode node) {
		super(node);
	}

}
