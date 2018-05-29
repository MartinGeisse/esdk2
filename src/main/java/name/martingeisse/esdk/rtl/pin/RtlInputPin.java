/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.pin;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.rtl.signal.RtlSignal;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlSignal {

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
