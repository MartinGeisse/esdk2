package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import name.martingeisse.mahdl.input.cm.SignalLikeDefinition_WithInitializer;

public final class SignalLikeDefinition_WithInitializerImpl extends SignalLikeDefinitionImpl implements SignalLikeDefinition_WithInitializer {

	private final CmToken identifier;
	private final ExtendedExpression initializer;

	public SignalLikeDefinition_WithInitializerImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.identifier = (CmToken) childNodes[0];
		((CmNodeImpl) this.identifier).setParent(this);
		this.initializer = (ExtendedExpression) childNodes[2];
		((CmNodeImpl) this.initializer).setParent(this);
	}

	public CmToken getIdentifier() {
		return identifier;
	}

	public ExtendedExpression getInitializer() {
		return initializer;
	}

}
