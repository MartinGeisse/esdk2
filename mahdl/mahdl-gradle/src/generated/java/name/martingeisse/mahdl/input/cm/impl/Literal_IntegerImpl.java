package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.Literal_Integer;

public final class Literal_IntegerImpl extends LiteralImpl implements Literal_Integer {

	private final CmToken value;

	public Literal_IntegerImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.value = (CmToken) childNodes[0];
		((CmNodeImpl) this.value).setParent(this);
	}

	public CmToken getValue() {
		return value;
	}

}
