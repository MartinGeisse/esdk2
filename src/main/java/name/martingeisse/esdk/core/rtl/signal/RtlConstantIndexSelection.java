/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;

/**
 * Unlike {@link RtlIndexSelection} with an {@link RtlVectorConstant} as the index, this class can select the upper
 * bits of a vector whose width is not a power-of-two. A non-constant selection cannot do that because it cannot
 * statically prove that the index is always within range.
 */
public final class RtlConstantIndexSelection extends RtlItem implements RtlBitSignal {

	private final RtlVectorSignal containerSignal;
	private final int index;

	public RtlConstantIndexSelection(RtlRealm realm, RtlVectorSignal containerSignal, int index) {
		super(realm);
		checkSameRealm(containerSignal);
		if (index >= containerSignal.getWidth()) {
			throw new IllegalArgumentException("index " + index + " out of bounds for width " + containerSignal.getWidth());
		}
		this.containerSignal = containerSignal;
		this.index = index;
	}

	public RtlVectorSignal getContainerSignal() {
		return containerSignal;
	}

	public int getIndex() {
		return index;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return containerSignal.getValue().select(index);
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
		out.print(index);
		out.print(']');
	}

}
