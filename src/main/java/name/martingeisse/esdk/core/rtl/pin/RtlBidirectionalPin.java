/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 * TODO generate tri-state assignment
 */
public final class RtlBidirectionalPin extends RtlPin implements RtlBitSignal {

	private final RtlSettableBitSignal settableInputBitSignal;
	private RtlBitSignal outputSignal;
	private RtlBitSignal outputEnableSignal;

	public RtlBidirectionalPin(RtlRealm realm) {
		super(realm);
		this.settableInputBitSignal = new RtlSettableBitSignal(realm);
	}

	public RtlSettableBitSignal getSettableInputBitSignal() {
		return settableInputBitSignal;
	}

	public RtlBitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlBitSignal outputSignal) {
		this.outputSignal = checkSameRealm(outputSignal);
	}

	public RtlBitSignal getOutputEnableSignal() {
		return outputEnableSignal;
	}

	public void setOutputEnableSignal(RtlBitSignal outputEnableSignal) {
		this.outputEnableSignal = checkSameRealm(outputEnableSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return outputEnableSignal.getValue() ? outputSignal.getValue() : settableInputBitSignal.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean isGenerateVerilogAssignmentForDeclaration() {
		return false;
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write an implementation expression for bidirectional pins");
	}

	@Override
	public String getVerilogDirectionKeyword() {
		return "inout";
	}

}
