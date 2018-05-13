/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import com.google.common.collect.ImmutableList;

/**
 *
 */
public final class RtlConcatenation extends RtlItem implements RtlVectorSignal {

	private final ImmutableList<RtlSignal> signals;
	private final int width;

	/**
	 * Unlike other object fields, the list of signals must be determined in advance. This is to ensure that the
	 * result width doesn't change.
	 */
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

}
