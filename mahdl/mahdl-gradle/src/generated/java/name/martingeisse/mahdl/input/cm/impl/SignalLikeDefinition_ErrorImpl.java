package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.SignalLikeDefinition_Error;

public final class SignalLikeDefinition_ErrorImpl extends SignalLikeDefinitionImpl implements SignalLikeDefinition_Error {

	private final CmToken identifier;

	public SignalLikeDefinition_ErrorImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.identifier = (CmToken) childNodes[0];
		((CmNodeImpl) this.identifier).setParent(this);
	}

	public CmToken getIdentifier() {
		return identifier;
	}

}
