/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlBitConstant extends RtlSignalBase implements RtlBitSignal {

	private final boolean value;

	public RtlBitConstant(RtlRealm realm, boolean value) {
		super(realm);
		this.value = value;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	public boolean getValue() {
		return value;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(value ? "1'b1" : "1'b0");
	}

}
