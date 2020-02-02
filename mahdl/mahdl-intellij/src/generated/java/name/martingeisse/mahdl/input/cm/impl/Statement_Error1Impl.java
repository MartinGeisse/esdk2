package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Statement_Error1;
import org.jetbrains.annotations.NotNull;

public final class Statement_Error1Impl extends StatementImpl implements Statement_Error1, PsiCm {

	public Statement_Error1Impl(@NotNull ASTNode node) {
		super(node);
	}

}
