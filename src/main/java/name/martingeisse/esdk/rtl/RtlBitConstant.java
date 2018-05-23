/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlBitConstant extends RtlItem implements RtlBitSignal {

	private final boolean value;

	public RtlBitConstant(RtlDesign design, boolean value) {
		super(design);
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(value ? "1'b1" : "1'b0");
	}

}
