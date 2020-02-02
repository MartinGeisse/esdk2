package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.FunctionName_Identifier;

public final class FunctionName_IdentifierImpl extends FunctionNameImpl implements FunctionName_Identifier {

	private final CmToken value;

	public FunctionName_IdentifierImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.value = (CmToken) childNodes[0];
		((CmNodeImpl) this.value).setParent(this);
	}

	public CmToken getValue() {
		return value;
	}

}
