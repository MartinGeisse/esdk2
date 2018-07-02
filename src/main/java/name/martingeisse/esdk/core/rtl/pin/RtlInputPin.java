/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.RtlDesign;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlBitSignal {

	public RtlInputPin(RtlDesign design) {
		super(design);
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(getNetName());
	}

	@Override
	public String getVerilogDirectionKeyword() {
		return "input";
	}

}