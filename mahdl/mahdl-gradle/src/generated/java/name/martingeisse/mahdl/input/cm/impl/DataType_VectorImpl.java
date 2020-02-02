package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.DataType_Vector;
import name.martingeisse.mahdl.input.cm.Expression;

public final class DataType_VectorImpl extends DataTypeImpl implements DataType_Vector {

	private final Expression size;

	public DataType_VectorImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.size = (Expression) childNodes[2];
		((CmNodeImpl) this.size).setParent(this);
	}

	public Expression getSize() {
		return size;
	}

}
