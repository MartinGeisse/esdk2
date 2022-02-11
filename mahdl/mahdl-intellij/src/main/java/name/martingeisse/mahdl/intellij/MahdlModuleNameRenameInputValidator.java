package name.martingeisse.mahdl.intellij;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.util.ProcessingContext;
import name.martingeisse.mahdl.input.cm.impl.ModuleImpl;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * TODO renaming modules and module files does not work yet. It's unclear to me why the rename PSI element processor
 * accepts local names and port names, but it works.
 */
public class MahdlModuleNameRenameInputValidator implements RenameInputValidator {

	private static final Pattern VALID_MODULE_NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*(\\.[a-zA-Z_][a-zA-Z_0-9]*)*");

	@NotNull
	@Override
	public ElementPattern<? extends PsiElement> getPattern() {
		return StandardPatterns.instanceOf(ModuleImpl.class);
	}

	@Override
	public boolean isInputValid(@NotNull String s, @NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
		return VALID_MODULE_NAME_PATTERN.matcher(s).matches();
	}

}
