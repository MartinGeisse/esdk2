/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.RtlBitAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlProceduralBitSignal extends RtlProceduralSignal implements RtlBitSignal, RtlBitAssignmentTarget {

	private boolean value;
	private boolean nextValue;

	public RtlProceduralBitSignal(RtlRealm realm, RtlClockedBlock block) {
		super(realm, block);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public void setNextValue(boolean nextValue) {
		this.nextValue = nextValue;
	}

	@Override
	void updateValue() {
		value = nextValue;
	}

}
