package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType;
import org.jetbrains.annotations.NotNull;

public abstract class DataTypeImpl extends ASTWrapperPsiElement implements DataType, PsiCm {

	public DataTypeImpl(@NotNull ASTNode node) {
		super(node);
	}

}
