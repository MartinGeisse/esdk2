package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.PortDirection_Out;
import org.jetbrains.annotations.NotNull;

public final class PortDirection_OutImpl extends PortDirectionImpl implements PortDirection_Out, PsiCm {

	public PortDirection_OutImpl(@NotNull ASTNode node) {
		super(node);
	}

}
