/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitConstant extends RtlItem implements RtlBitSignal {

	private final boolean value;

	public RtlBitConstant(RtlDomain domain, boolean value) {
		super(domain);
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
