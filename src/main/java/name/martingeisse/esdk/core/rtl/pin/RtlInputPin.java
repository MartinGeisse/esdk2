/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlBitSignal {

	public RtlInputPin(RtlDomain domain) {
		super(domain);
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(getNetName());
	}

	@Override
	public String getVerilogDirectionKeyword() {
		return "input";
	}

	@Override
	public boolean getValue() {
		throw new UnsupportedOperationException("cannot use RtlInputPin in simulation");
	}

}
