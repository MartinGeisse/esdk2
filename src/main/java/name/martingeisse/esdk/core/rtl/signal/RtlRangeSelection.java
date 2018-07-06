/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public class RtlRangeSelection extends RtlItem implements RtlVectorSignal {

	private final RtlVectorSignal containerSignal;
	private final int from;
	private final int to;

	public RtlRangeSelection(RtlDomain design, RtlVectorSignal containerSignal, int from, int to) {
		super(design);
		checkSameDomain(containerSignal);
		if (from < 0 || to < 0 || from >= containerSignal.getWidth() || to >= containerSignal.getWidth() || from < to) {
			throw new IllegalArgumentException("invalid from/to indices for container width " +
				containerSignal.getWidth() + ": from = " + from + ", to = " + to);
		}
		this.containerSignal = containerSignal;
		this.from = from;
		this.to = to;
	}

	public RtlVectorSignal getContainerSignal() {
		return containerSignal;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int getWidth() {
		return from - to + 1;
	}

	@Override
	public boolean compliesWith(VerilogDesignGenerator.VerilogExpressionNesting nesting) {
		return nesting != VerilogDesignGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(containerSignal, VerilogDesignGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.print(from);
		out.print(':');
		out.print(to);
		out.print(']');
	}

	@Override
	public VectorValue getValue() {
		return containerSignal.getValue().select(from, to);
	}

}
