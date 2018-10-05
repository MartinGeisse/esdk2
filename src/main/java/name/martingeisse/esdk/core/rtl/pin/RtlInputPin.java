/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;

/**
 *
 */
public final class RtlInputPin extends RtlPin implements RtlBitSignal {

	private final RtlSettableBitSignal settableBitSignal;

	public RtlInputPin(RtlRealm realm) {
		super(realm);
		this.settableBitSignal = new RtlSettableBitSignal(realm);
	}

	public RtlSettableBitSignal getSettableBitSignal() {
		return settableBitSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return settableBitSignal.getValue();
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
		throw new UnsupportedOperationException("cannot write an implementation expression for input pins");
	}

	@Override
	public String getVerilogDirectionKeyword() {
		return "input";
	}

}
