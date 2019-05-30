/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

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
		if (index < 0 || index >= containerSignal.getWidth()) {
			throw new IllegalArgumentException("index " + index + " out of bounds for width " + containerSignal.getWidth());
		}
		this.containerSignal = checkSameRealm(containerSignal);
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
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting != VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(containerSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		if (containerSignal.getWidth() > 1) {
			out.print('[');
			out.print(index);
			out.print(']');
		}
	}

}
