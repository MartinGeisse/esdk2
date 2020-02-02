package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType_Vector;
import name.martingeisse.mahdl.input.cm.Expression;
import org.jetbrains.annotations.NotNull;

public final class DataType_VectorImpl extends DataTypeImpl implements DataType_Vector, PsiCm {

	public DataType_VectorImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getSize() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getSizePsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

}
