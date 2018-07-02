/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorValue;
import name.martingeisse.esdk.core.rtl.statement.RtlVectorAssignmentTarget;

/**
 *
 */
public final class RtlProceduralVectorSignal extends RtlProceduralSignal implements RtlVectorSignal, RtlVectorAssignmentTarget {

	private final int width;
	private RtlVectorValue value;

	public RtlProceduralVectorSignal(RtlDesign design, RtlBlock block, int width) {
		super(design, block);
		this.width = width;
		this.value = RtlVectorValue.zeroes(width);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public RtlVectorValue getValue() {
		return value;
	}

	// TODO distinguish current / next value
	void setValue(RtlVectorValue value) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("trying to set value of wrong width " + value.getWidth() + ", should be " + width);
		}
		this.value = value;
	}

}
