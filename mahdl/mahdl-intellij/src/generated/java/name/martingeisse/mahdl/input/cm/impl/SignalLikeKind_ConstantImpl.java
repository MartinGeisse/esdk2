package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.SignalLikeKind_Constant;
import org.jetbrains.annotations.NotNull;

public final class SignalLikeKind_ConstantImpl extends SignalLikeKindImpl implements SignalLikeKind_Constant, PsiCm {

	public SignalLikeKind_ConstantImpl(@NotNull ASTNode node) {
		super(node);
	}

}
