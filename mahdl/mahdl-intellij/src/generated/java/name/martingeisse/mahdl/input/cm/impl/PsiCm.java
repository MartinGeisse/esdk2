package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.psi.PsiElement;
import name.martingeisse.mahdl.input.cm.CmNode;

public interface PsiCm extends CmNode {

	default PsiElement getPsi() {
		return InternalPsiUtil.getPsiFromCm(this);
	}

	@Override
	default CmNode getCmParent() {
		PsiElement thisPsi = getPsi();
		if (thisPsi == null) {
			return null;
		}
		PsiElement parentPsi = thisPsi.getParent();
		if (parentPsi == null) {
			return null;
		}
		return InternalPsiUtil.getCmFromPsi(parentPsi);
	}

	default int compareStartOffset(CmNode other) {
		int thisOffset = getPsi().getTextRange().getStartOffset();
		int otherOffset = InternalPsiUtil.getPsiFromCm(other).getTextRange().getStartOffset();
		return thisOffset - otherOffset;
	}

}
