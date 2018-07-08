/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRegion;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 * TODO generate tri-state assignment
 */
public final class RtlBidirectionalPin extends RtlPin implements RtlBitSignal {

	private final RtlSettableBitSignal settableInputBitSignal;
	private RtlBitSignal outputSignal;
	private RtlBitSignal outputEnableSignal;

	public RtlBidirectionalPin(RtlRegion region) {
		super(region);
		this.settableInputBitSignal = new RtlSettableBitSignal(region);
	}

	public RtlSettableBitSignal getSettableInputBitSignal() {
		return settableInputBitSignal;
	}

	public RtlBitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlBitSignal outputSignal) {
		this.outputSignal = outputSignal;
	}

	public RtlBitSignal getOutputEnableSignal() {
		return outputEnableSignal;
	}

	public void setOutputEnableSignal(RtlBitSignal outputEnableSignal) {
		this.outputEnableSignal = outputEnableSignal;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(getNetName());
	}

	@Override
	public String getVerilogDirectionKeyword() {
		return "inout";
	}

	@Override
	public boolean getValue() {
		return outputEnableSignal.getValue() ? outputSignal.getValue() : settableInputBitSignal.getValue();
	}

}
