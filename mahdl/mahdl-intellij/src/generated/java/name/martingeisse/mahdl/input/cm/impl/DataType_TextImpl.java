package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType_Text;
import org.jetbrains.annotations.NotNull;

public final class DataType_TextImpl extends DataTypeImpl implements DataType_Text, PsiCm {

	public DataType_TextImpl(@NotNull ASTNode node) {
		super(node);
	}

}
