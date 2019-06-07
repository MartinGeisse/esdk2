/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Note: Unlike other object fields, the list of signals must be determined in advance. This is to ensure that the
 * result width doesn't change.
 *
 * TODO have zero-width operands by removing them; possibly remove the concat after that; what if all are zero-width?
 */
public final class RtlConcatenation extends RtlItem implements RtlVectorSignal {

	private final ImmutableList<RtlSignal> signals;
	private final int width;

	public RtlConcatenation(RtlRealm realm, RtlSignal... signals) {
		this(realm, ImmutableList.copyOf(signals));
	}

	public RtlConcatenation(RtlRealm realm, ImmutableList<RtlSignal> signals) {
		super(realm);

		// store signals
		for (RtlSignal signal : signals) {
			checkSameRealm(signal);
		}
		this.signals = signals;

		// precompute total width for faster access
		int width = 0;
		for (RtlSignal signal : signals) {
			if (signal instanceof RtlBitSignal) {
				width++;
			} else if (signal instanceof RtlVectorSignal) {
				width += ((RtlVectorSignal) signal).getWidth();
			} else {
				throw new IllegalArgumentException("list of signals contains unknown signal type: " + signal);
			}
		}
		this.width = width;

	}

	public ImmutableList<RtlSignal> getSignals() {
		return signals;
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		VectorValue result = VectorValue.of(0, 0);
		for (RtlSignal elementSignal : signals) {
			if (elementSignal instanceof RtlBitSignal) {
				result = result.concat(((RtlBitSignal) elementSignal).getValue());
			} else if (elementSignal instanceof RtlVectorSignal) {
				result = result.concat(((RtlVectorSignal) elementSignal).getValue());
			} else {
				throw new RuntimeException("invalid signal: " + elementSignal);
			}
		}
		return result;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		if (width == 0) {
			throw new RuntimeException("cannot print zero-width concatenation");
		}
		out.print('{');
		boolean first = true;
		for (RtlSignal signal : signals) {
			if (first) {
				first = false;
			} else {
				out.print(", ");
			}
			out.print(signal, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		}
		out.print('}');
	}

}
