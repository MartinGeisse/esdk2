package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.SignalLikeKind_Signal;
import org.jetbrains.annotations.NotNull;

public final class SignalLikeKind_SignalImpl extends SignalLikeKindImpl implements SignalLikeKind_Signal, PsiCm {

	public SignalLikeKind_SignalImpl(@NotNull ASTNode node) {
		super(node);
	}

}
