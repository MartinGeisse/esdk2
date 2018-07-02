/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

import java.util.BitSet;

/**
 * Note: Unlike other object fields, the list of signals must be determined in advance. This is to ensure that the
 * result width doesn't change.
 */
public final class RtlConcatenation extends RtlItem implements RtlVectorSignal {

	private final ImmutableList<RtlSignal> signals;
	private final int width;

	public RtlConcatenation(RtlDesign design, RtlSignal... signals) {
		this(design, ImmutableList.copyOf(signals));
	}

	public RtlConcatenation(RtlDesign design, ImmutableList<RtlSignal> signals) {
		super(design);

		// store signals
		for (RtlSignal signal : signals) {
			checkSameDesign(signal);
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

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
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
			out.print(signal, VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		}
		out.print('}');
	}

	@Override
	public RtlVectorValue getValue() {
		BitSet result = new BitSet();
		int previousWidth = 0;
		for (int signalIndex = signals.size() - 1; signalIndex >= 0; signalIndex--) {
			RtlSignal elementSignal = signals.get(signalIndex);
			if (elementSignal instanceof RtlBitSignal) {
				result.set(previousWidth, ((RtlBitSignal) elementSignal).getValue());
				previousWidth++;
			} else if (elementSignal instanceof RtlVectorSignal) {
				RtlVectorValue elementValue = ((RtlVectorSignal) elementSignal).getValue();
				BitSet elementBits = elementValue.getBitsShared();
				for (int i = 0; i < elementValue.getWidth(); i++) {
					result.set(previousWidth + i, elementBits.get(i));
				}
				previousWidth += elementValue.getWidth();
			} else {
				throw new RuntimeException("invalid signal: " + elementSignal);
			}
		}
		return new RtlVectorValue(previousWidth, result, false);
	}

}