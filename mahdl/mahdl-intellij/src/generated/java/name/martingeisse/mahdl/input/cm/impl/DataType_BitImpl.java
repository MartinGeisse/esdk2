package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType_Bit;
import org.jetbrains.annotations.NotNull;

public final class DataType_BitImpl extends DataTypeImpl implements DataType_Bit, PsiCm {

	public DataType_BitImpl(@NotNull ASTNode node) {
		super(node);
	}

}
