package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.FunctionName_Bit;
import org.jetbrains.annotations.NotNull;

public final class FunctionName_BitImpl extends FunctionNameImpl implements FunctionName_Bit, PsiCm {

	public FunctionName_BitImpl(@NotNull ASTNode node) {
		super(node);
	}

}
