package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType_Clock;
import org.jetbrains.annotations.NotNull;

public final class DataType_ClockImpl extends DataTypeImpl implements DataType_Clock, PsiCm {

	public DataType_ClockImpl(@NotNull ASTNode node) {
		super(node);
	}

}
