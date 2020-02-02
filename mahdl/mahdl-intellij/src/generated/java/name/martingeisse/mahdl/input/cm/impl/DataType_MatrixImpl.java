package name.martingeisse.mahdl.input.cm.impl;

import com.intellij.lang.ASTNode;
import name.martingeisse.mahdl.input.cm.DataType_Matrix;
import name.martingeisse.mahdl.input.cm.Expression;
import org.jetbrains.annotations.NotNull;

public final class DataType_MatrixImpl extends DataTypeImpl implements DataType_Matrix, PsiCm {

	public DataType_MatrixImpl(@NotNull ASTNode node) {
		super(node);
	}

	public Expression getFirstSize() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 2));
	}

	public ExpressionImpl getFirstSizePsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 2);
	}

	public Expression getSecondSize() {
		return (Expression) InternalPsiUtil.getCmFromPsi(InternalPsiUtil.getChild(this, 5));
	}

	public ExpressionImpl getSecondSizePsi() {
		return (ExpressionImpl) InternalPsiUtil.getChild(this, 5);
	}

}
