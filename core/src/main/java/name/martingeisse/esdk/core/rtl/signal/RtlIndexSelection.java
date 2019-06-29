/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public final class RtlIndexSelection extends RtlItem implements RtlBitSignal {

	private final RtlVectorSignal containerSignal;
	private final RtlVectorSignal indexSignal;

	public RtlIndexSelection(RtlRealm realm, RtlVectorSignal containerSignal, RtlVectorSignal indexSignal) {
		super(realm);
		if (containerSignal.getWidth() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("container of width " + containerSignal.getWidth() + " is too small for index of width " + indexSignal.getWidth());
		}
		this.containerSignal = checkSameRealm(containerSignal);
		this.indexSignal = checkSameRealm(indexSignal);
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
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting != VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.printSignal(containerSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.printSignal(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print(']');
	}

}
