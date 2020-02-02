package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.Statement_Error2;
import org.jetbrains.annotations.NotNull;

public final class Statement_Error2Impl extends StatementImpl implements Statement_Error2, PsiCm {

	public Statement_Error2Impl(@NotNull ASTNode node) {
		super(node);
	}

}
