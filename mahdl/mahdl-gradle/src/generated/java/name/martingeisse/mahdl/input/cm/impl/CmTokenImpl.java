package name.martingeisse.mahdl.input.cm.impl;

import name.martingeisse.mahdl.input.cm.CmToken;

public final class CmTokenImpl extends CmNodeImpl implements CmToken {

	private final String text;
	private final IElementType elementType;

	public CmTokenImpl(int row, int column, String text, IElementType elementType) {
		super(row, column);
		this.text = text;
		this.elementType = elementType;
	}

	public String getText() {
		return text;
	}

	public IElementType getElementType() {
		return elementType;
	}

}
