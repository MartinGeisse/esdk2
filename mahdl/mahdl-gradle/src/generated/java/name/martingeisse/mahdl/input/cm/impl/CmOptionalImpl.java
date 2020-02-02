package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.CmOptional;

public final class CmOptionalImpl<T extends CmNode> extends CmNodeImpl implements CmOptional<T> {

	private final T it;

	public CmOptionalImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.it = (childNodes.length == 0 ? null : (T) childNodes[0]);
		if (it != null) {
			((CmNodeImpl) it).setParent(this);
		}
	}

	public T getIt() {
		return it;
	}

}
