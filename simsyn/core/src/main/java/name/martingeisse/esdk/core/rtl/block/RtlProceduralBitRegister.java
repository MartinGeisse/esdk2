/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlBitAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlProceduralBitRegister extends RtlProceduralRegister implements RtlBitSignal, RtlBitAssignmentTarget {

	private boolean value;
	private boolean nextValue;

	public RtlProceduralBitRegister(RtlRealm realm, RtlClockedBlock block) {
		super(realm, block);
	}

	public RtlProceduralBitRegister(RtlRealm realm, RtlClockedBlock block, boolean initialValue) {
		super(realm, block);
		this.value = initialValue;
		this.nextValue = initialValue;
		setInitialized(true);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public boolean getNextValue() {
		return nextValue;
	}

	@Override
	public void setNextValue(boolean nextValue) {
		this.nextValue = nextValue;
	}

	@Override
	void updateValue() {
		value = nextValue;
	}

}
