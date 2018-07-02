/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public final class RtlVectorAssignment extends RtlAssignment {

	private final RtlVectorAssignmentTarget destination;
	private final RtlVectorSignal source;

	public RtlVectorAssignment(RtlDesign design, RtlVectorAssignmentTarget destination, RtlVectorSignal source) {
		super(design);
		checkSameDesign(destination);
		checkSameDesign(source);
		if (destination.getWidth() != source.getWidth()) {
			throw new IllegalArgumentException("destination width (" + destination.getWidth() + ") and source width (" + source.getWidth() + ") differ");
		}
		this.destination = destination;
		this.source = source;
	}

	@Override
	public RtlVectorAssignmentTarget getDestination() {
		return destination;
	}

	@Override
	public RtlVectorSignal getSource() {
		return source;
	}

	@Override
	public void execute() {
		destination.setNextValue(source.getValue());
	}

}
