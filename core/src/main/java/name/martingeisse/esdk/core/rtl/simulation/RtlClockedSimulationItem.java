/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 * Like {@link RtlClockedItem}, but prevents synthesis.
 */
public abstract class RtlClockedSimulationItem extends RtlClockedItem {

	public RtlClockedSimulationItem(RtlClockNetwork clockNetwork) {
		super(clockNetwork);
	}

	@Override
	public final VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
