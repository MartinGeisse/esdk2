package name.martingeisse.mahdl.intellij;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class MahdlModuleNameRenamePsiElementProcessor extends RenamePsiElementProcessor {

	@Override
	public boolean canProcessElement(@NotNull PsiElement psiElement) {
		return (psiElement instanceof ModuleImpl);
	}

}
