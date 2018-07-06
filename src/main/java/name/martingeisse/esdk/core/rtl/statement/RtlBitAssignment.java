/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlBitAssignment extends RtlAssignment {

	private final RtlBitAssignmentTarget destination;
	private final RtlBitSignal source;

	public RtlBitAssignment(RtlDomain design, RtlBitAssignmentTarget destination, RtlBitSignal source) {
		super(design);
		checkSameDomain(destination);
		checkSameDomain(source);
		this.destination = destination;
		this.source = source;
	}

	@Override
	public RtlBitAssignmentTarget getDestination() {
		return destination;
	}

	@Override
	public RtlBitSignal getSource() {
		return source;
	}

	@Override
	public void execute() {
		destination.setNextValue(source.getValue());
	}

}
