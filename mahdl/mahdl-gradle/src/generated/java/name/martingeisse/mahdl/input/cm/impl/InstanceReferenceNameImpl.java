package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.InstanceReferenceName;

public final class InstanceReferenceNameImpl extends CmNodeImpl implements InstanceReferenceName {

	private final CmToken identifier;

	public InstanceReferenceNameImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.identifier = (CmToken) childNodes[0];
		((CmNodeImpl) this.identifier).setParent(this);
	}

	public CmToken getIdentifier() {
		return identifier;
	}

}
