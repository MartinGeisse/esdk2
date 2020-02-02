package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.PortDirection_In;
import org.jetbrains.annotations.NotNull;

public final class PortDirection_InImpl extends PortDirectionImpl implements PortDirection_In, PsiCm {

	public PortDirection_InImpl(@NotNull ASTNode node) {
		super(node);
	}

}
