package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmNode;

public abstract class CmNodeImpl implements CmNode {

	private final int row;
	private final int column;
	private CmNode parent;

	public CmNodeImpl(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	void setParent(CmNode parent) {
		this.parent = parent;
	}

	public CmNode getCmParent() {
		return parent;
	}

	public int compareStartOffset(CmNode other) {
		CmNodeImpl otherImpl = (CmNodeImpl) other;
		return (row != otherImpl.row) ? (row - otherImpl.row) : (column - otherImpl.column);
	}

}
