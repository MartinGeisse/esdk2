/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.*;

/**
 *
 */
public final class RtlProceduralVectorSignalAssignment extends RtlProceduralSignalAssignment {

	private final RtlProceduralVectorSignal destination;
	private final RtlVectorSignal source;

	public RtlProceduralVectorSignalAssignment(RtlDesign design, RtlProceduralVectorSignal destination, RtlVectorSignal source) {
		super(design);
		if (destination.getWidth() != source.getWidth()) {
			throw new IllegalArgumentException("destination width (" + destination.getWidth() + ") and source width (" + source.getWidth() + ") differ");
		}
		this.destination = destination;
		this.source = source;
	}

	@Override
	public RtlProceduralVectorSignal getDestination() {
		return destination;
	}

	@Override
	public RtlVectorSignal getSource() {
		return source;
	}

}
