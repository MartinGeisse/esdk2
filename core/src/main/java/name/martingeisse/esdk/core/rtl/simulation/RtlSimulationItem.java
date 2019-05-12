/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 * Like {@link RtlItem}, but prevents synthesis.
 */
public abstract class RtlSimulationItem extends RtlItem {

	public RtlSimulationItem(RtlRealm realm) {
		super(realm);
	}

	@Override
	public final VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
