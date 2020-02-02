package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType_Integer;
import org.jetbrains.annotations.NotNull;

public final class DataType_IntegerImpl extends DataTypeImpl implements DataType_Integer, PsiCm {

	public DataType_IntegerImpl(@NotNull ASTNode node) {
		super(node);
	}

}
