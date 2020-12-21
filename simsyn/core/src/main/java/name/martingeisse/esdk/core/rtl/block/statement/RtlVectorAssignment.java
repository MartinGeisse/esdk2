/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.getter.DefaultSignalGetterFactory;
import name.martingeisse.esdk.core.rtl.signal.getter.RtlBitSignalGetter;
import name.martingeisse.esdk.core.rtl.signal.getter.RtlVectorSignalGetter;

/**
 *
 */
public final class RtlVectorAssignment extends RtlAssignment {

	private final RtlVectorAssignmentTarget destination;
	private final RtlVectorSignal source;
	private RtlVectorSignalGetter sourceGetter;

	public RtlVectorAssignment(RtlRealm realm, RtlVectorAssignmentTarget destination, RtlVectorSignal source) {
		super(realm);
		if (destination.getWidth() != source.getWidth()) {
			throw new IllegalArgumentException("destination width (" + destination.getWidth() + ") and source width (" + source.getWidth() + ") differ");
		}
		this.destination = checkSameRealm(destination);
		this.source = checkSameRealm(source);
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
