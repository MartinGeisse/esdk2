/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlIndexSelection extends RtlItem implements RtlBitSignal {

	private final RtlVectorSignal containerSignal;
	private final RtlVectorSignal indexSignal;

	public RtlIndexSelection(RtlRealm realm, RtlVectorSignal containerSignal, RtlVectorSignal indexSignal) {
		super(realm);
		checkSameRealm(containerSignal);
		checkSameRealm(indexSignal);
		if (containerSignal.getWidth() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("container of width " + containerSignal.getWidth() + " is too small for index of width " + indexSignal.getWidth());
		}
		this.containerSignal = containerSignal;
		this.indexSignal = indexSignal;
	}

	public RtlVectorSignal getContainerSignal() {
		return containerSignal;
	}

	public RtlVectorSignal getIndexSignal() {
		return indexSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return containerSignal.getValue().select(indexSignal.getValue().getAsUnsignedInt());
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean compliesWith(VerilogGenerator.VerilogExpressionNesting nesting) {
		return nesting != VerilogGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(containerSignal, VerilogGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.print(indexSignal, VerilogGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print(']');
	}

}
