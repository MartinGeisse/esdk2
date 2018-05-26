/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public class RtlRangeSelection extends RtlItem implements RtlVectorSignal {

	private final RtlVectorSignal containerSignal;
	private final int from;
	private final int to;

	public RtlRangeSelection(RtlDesign design, RtlVectorSignal containerSignal, int from, int to) {
		super(design);
		checkSameDesign(containerSignal);
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

}
