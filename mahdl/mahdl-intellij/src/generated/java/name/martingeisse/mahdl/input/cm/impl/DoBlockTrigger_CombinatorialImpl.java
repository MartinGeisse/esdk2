package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DoBlockTrigger_Combinatorial;
import org.jetbrains.annotations.NotNull;

public final class DoBlockTrigger_CombinatorialImpl extends DoBlockTriggerImpl implements DoBlockTrigger_Combinatorial, PsiCm {

	public DoBlockTrigger_CombinatorialImpl(@NotNull ASTNode node) {
		super(node);
	}

}
