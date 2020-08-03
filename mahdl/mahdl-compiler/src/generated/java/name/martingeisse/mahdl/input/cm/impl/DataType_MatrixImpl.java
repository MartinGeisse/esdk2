package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.DataType_Matrix;
import name.martingeisse.mahdl.input.cm.Expression;

public final class DataType_MatrixImpl extends DataTypeImpl implements DataType_Matrix {

	private final Expression firstSize;
	private final Expression secondSize;

	public DataType_MatrixImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.firstSize = (Expression) childNodes[2];
		((CmNodeImpl) this.firstSize).setParent(this);
		this.secondSize = (Expression) childNodes[5];
		((CmNodeImpl) this.secondSize).setParent(this);
	}

	public Expression getFirstSize() {
		return firstSize;
	}

	public Expression getSecondSize() {
		return secondSize;
	}

}
