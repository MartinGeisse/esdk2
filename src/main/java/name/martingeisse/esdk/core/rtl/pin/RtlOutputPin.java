/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public final class RtlOutputPin extends RtlPin {

	private RtlBitSignal outputSignal;

	public RtlOutputPin(RtlRealm realm) {
		super(realm);
	}

	public RtlBitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(RtlBitSignal outputSignal) {
		this.outputSignal = checkSameRealm(outputSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public String getVerilogDirectionKeyword() {
		return "output";
	}

}
