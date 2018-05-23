/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlBitNotOperation extends RtlItem implements RtlBitSignal {

	private final RtlBitSignal operand;

	public RtlBitNotOperation(RtlDesign design, RtlBitSignal operand) {
		super(design);
		this.operand = operand;
	}

	public RtlBitSignal getOperand() {
		return operand;
	}

}
