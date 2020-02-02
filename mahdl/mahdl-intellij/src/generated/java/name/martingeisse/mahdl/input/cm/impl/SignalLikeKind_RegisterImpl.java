package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.SignalLikeKind_Register;
import org.jetbrains.annotations.NotNull;

public final class SignalLikeKind_RegisterImpl extends SignalLikeKindImpl implements SignalLikeKind_Register, PsiCm {

	public SignalLikeKind_RegisterImpl(@NotNull ASTNode node) {
		super(node);
	}

}
