package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.SignalLikeDefinition_WithoutInitializer;

public final class SignalLikeDefinition_WithoutInitializerImpl extends SignalLikeDefinitionImpl implements SignalLikeDefinition_WithoutInitializer {

	private final CmToken identifier;

	public SignalLikeDefinition_WithoutInitializerImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.identifier = (CmToken) childNodes[0];
		((CmNodeImpl) this.identifier).setParent(this);
	}

	public CmToken getIdentifier() {
		return identifier;
	}

}
