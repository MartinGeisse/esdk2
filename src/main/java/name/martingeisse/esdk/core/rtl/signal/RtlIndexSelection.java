/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.RtlItem;

/**
 *
 */
public final class RtlIndexSelection extends RtlItem implements RtlBitSignal {

	private final RtlVectorSignal containerSignal;
	private final RtlVectorSignal indexSignal;

	public RtlIndexSelection(RtlDesign design, RtlVectorSignal containerSignal, RtlVectorSignal indexSignal) {
		super(design);
		checkSameDesign(containerSignal);
		checkSameDesign(indexSignal);
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

	@Override
	public boolean compliesWith(VerilogDesignGenerator.VerilogExpressionNesting nesting) {
		return nesting != VerilogDesignGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(containerSignal, VerilogDesignGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.print(indexSignal, VerilogDesignGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print(']');
	}

	@Override
	public boolean getValue() {
		return containerSignal.getValue().getBit(indexSignal.getValue().convertUnsignedToSmallInteger());
	}

}
