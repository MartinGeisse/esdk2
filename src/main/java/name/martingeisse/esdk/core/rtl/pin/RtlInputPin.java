/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRegion;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlBitSignal {

	private final RtlSettableBitSignal settableBitSignal;

	public RtlInputPin(RtlRegion region) {
		super(region);
		this.settableBitSignal = new RtlSettableBitSignal(region);
	}

	public RtlSettableBitSignal getSettableBitSignal() {
		return settableBitSignal;
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
		return settableBitSignal.getValue();
	}

}
