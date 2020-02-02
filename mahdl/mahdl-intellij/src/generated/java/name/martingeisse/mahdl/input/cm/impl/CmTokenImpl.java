package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.psi.impl.source.tree.LeafPsiElement;
import name.martingeisse.mahdl.input.cm.CmToken;

public final class CmTokenImpl implements PsiCm, CmToken {

	private final LeafPsiElement psi;

	public CmTokenImpl(LeafPsiElement psi) {
		this.psi = psi;
	}

	@Override
	public LeafPsiElement getPsi() {
		return psi;
	}

	@Override
	public String getText() {
		return psi.getText();
	}

}
