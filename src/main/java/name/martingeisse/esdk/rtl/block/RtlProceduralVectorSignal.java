/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.block;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.rtl.statement.RtlVectorAssignmentTarget;

/**
 *
 */
public final class RtlProceduralVectorSignal extends RtlProceduralSignal implements RtlVectorSignal, RtlVectorAssignmentTarget {

	private final int width;

	public RtlProceduralVectorSignal(RtlDesign design, RtlBlock block, int width) {
		super(design, block);
		this.width = width;
	}

	@Override
	public int getWidth() {
		return width;
	}

}
