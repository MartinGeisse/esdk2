package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.PortDefinition;

public final class PortDefinitionImpl extends CmNodeImpl implements PortDefinition {

	private final CmToken identifier;

	public PortDefinitionImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.identifier = (CmToken) childNodes[0];
		((CmNodeImpl) this.identifier).setParent(this);
	}

	public CmToken getIdentifier() {
		return identifier;
	}

}
