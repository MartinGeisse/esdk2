/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public class RtlRangeSelection extends RtlSignalBase implements RtlVectorSignal {

	private final RtlVectorSignal containerSignal;
	private final int from;
	private final int to;

	public RtlRangeSelection(RtlRealm realm, RtlVectorSignal containerSignal, int from, int to) {
		super(realm);
		if (from < 0 || to < 0 || from >= containerSignal.getWidth() || to >= containerSignal.getWidth() || from < to) {
			throw new IllegalArgumentException("invalid from/to indices for container width " +
				containerSignal.getWidth() + ": from = " + from + ", to = " + to);
		}
		this.containerSignal = checkSameRealm(containerSignal);
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

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return containerSignal.getValue().select(from, to);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting != VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(containerSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.print(from);
		out.print(':');
		out.print(to);
		out.print(']');
	}

}
