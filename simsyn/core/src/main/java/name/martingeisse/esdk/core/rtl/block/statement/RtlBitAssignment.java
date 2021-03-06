/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlBitAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.getter.DefaultSignalGetterFactory;
import name.martingeisse.esdk.core.rtl.signal.getter.RtlBitSignalGetter;

/**
 *
 */
public final class RtlBitAssignment extends RtlAssignment {

	private final RtlBitAssignmentTarget destination;
	private final RtlBitSignal source;
	private RtlBitSignalGetter sourceGetter;

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

	@Override
	public boolean isEffectivelyNop() {
		return false;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		super.initializeSimulation();
		this.sourceGetter = DefaultSignalGetterFactory.getGetter(source);
	}

	@Override
	public void execute() {
		destination.setNextValue(sourceGetter.getValue());
	}

}
