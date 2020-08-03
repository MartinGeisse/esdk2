package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.Expression_Identifier;

public final class Expression_IdentifierImpl extends ExpressionImpl implements Expression_Identifier {

	private final CmToken identifier;

	public Expression_IdentifierImpl(int row, int column, Object[] childNodes) {
		super(row, column);
		this.identifier = (CmToken) childNodes[0];
		((CmNodeImpl) this.identifier).setParent(this);
	}

	public CmToken getIdentifier() {
		return identifier;
	}

}
