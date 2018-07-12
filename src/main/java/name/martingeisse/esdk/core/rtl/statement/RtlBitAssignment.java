/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlBitAssignment extends RtlAssignment {

	private final RtlBitAssignmentTarget destination;
	private final RtlBitSignal source;

	public RtlBitAssignment(RtlRealm realm, RtlBitAssignmentTarget destination, RtlBitSignal source) {
		super(realm);
		this.destination = checkSameRealm(destination);
		this.source = checkSameRealm(source);
	}

	@Override
	public RtlBitAssignmentTarget getDestination() {
		return destination;
	}

	@Override
	public RtlBitSignal getSource() {
		return source;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
		destination.setNextValue(source.getValue());
	}

}
