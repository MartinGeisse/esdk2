/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 * TODO generate tri-state assignment
 */
public final class RtlBidirectionalPin extends RtlPin implements RtlSignal {

	private RtlSignal outputSignal;
	private RtlSignal outputEnableSignal;

	public RtlBidirectionalPin(RtlDesign design) {
		super(design);
	}

	public RtlSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlSignal outputSignal) {
		this.outputSignal = outputSignal;
	}

	public RtlSignal getOutputEnableSignal() {
		return outputEnableSignal;
	}

	public void setOutputEnableSignal(RtlSignal outputEnableSignal) {
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

}
