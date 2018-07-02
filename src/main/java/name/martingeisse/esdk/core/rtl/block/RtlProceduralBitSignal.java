/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.statement.RtlBitAssignmentTarget;

/**
 *
 */
public final class RtlProceduralBitSignal extends RtlProceduralSignal implements RtlBitSignal, RtlBitAssignmentTarget {

	private boolean value;

	public RtlProceduralBitSignal(RtlDesign design, RtlBlock block) {
		super(design, block);
	}

	// TODO distinguish current / next value
	void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public boolean getValue() {
		return value;
	}

}
