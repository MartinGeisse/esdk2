package name.martingeisse.mahdl.intellij;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class MahdlModuleNameRenamePsiElementProcessor extends RenamePsiElementProcessor {

	/**
	 * This method indicates whether this processor can handle the element, not whether it is possible to rename the
	 * element in general. If it returns false, then standard renaming takes place which is quite good for PSI reference
	 * elements -- all occurences get renamed. The only thing that even demands a specialized processor is that
	 * renaming a module should also rename the file.
	 *
	 * The standard processor is simply an instance of the base class with no methods overridden except
	 * that it can handle all elements.
	 */
	@Override
	public boolean canProcessElement(@NotNull PsiElement psiElement) {
		return (psiElement instanceof ModuleImpl);
	}

	@Override
	public @Nullable Runnable getPostRenameCallback(@NotNull PsiElement element, @NotNull String newName, @NotNull RefactoringElementListener elementListener) {
		// TODO rename the file -- or do that in the PSI reference?
		return null;
	}

}
