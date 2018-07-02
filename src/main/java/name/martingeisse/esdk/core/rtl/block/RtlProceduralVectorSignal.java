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
	private RtlVectorValue nextValue;

	public RtlProceduralVectorSignal(RtlDesign design, RtlBlock block, int width) {
		super(design, block);
		this.width = width;
		this.value = RtlVectorValue.zeroes(width);
		this.nextValue = value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public RtlVectorValue getValue() {
		return value;
	}

	public void setNextValue(RtlVectorValue nextValue) {
		if (nextValue == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (nextValue.getWidth() != width) {
			throw new IllegalArgumentException("trying to set next value of wrong width " + nextValue.getWidth() + ", should be " + width);
		}
		this.nextValue = nextValue;
	}

	@Override
	public boolean updateValue() {
		boolean changed = !value.equals(nextValue);
		value = nextValue;
		return changed;
	}

}
