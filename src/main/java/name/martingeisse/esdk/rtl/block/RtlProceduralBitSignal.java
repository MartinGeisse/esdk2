/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.block;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.rtl.statement.RtlBitAssignmentTarget;

/**
 *
 */
public final class RtlProceduralBitSignal extends RtlProceduralSignal implements RtlBitSignal, RtlBitAssignmentTarget {

	public RtlProceduralBitSignal(RtlDesign design, RtlBlock block) {
		super(design, block);
	}

}
