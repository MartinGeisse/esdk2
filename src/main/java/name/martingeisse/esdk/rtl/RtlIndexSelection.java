/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public class RtlIndexSelection extends RtlItem implements RtlBitSignal {

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

}
