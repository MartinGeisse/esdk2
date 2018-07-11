/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.statement.RtlVectorAssignmentTarget;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlProceduralVectorSignal extends RtlProceduralSignal implements RtlVectorSignal, RtlVectorAssignmentTarget {

	private final int width;
	private VectorValue value;
	private VectorValue nextValue;

	public RtlProceduralVectorSignal(RtlRealm realm, RtlClockedBlock block, int width) {
		super(realm, block);
		this.width = width;
		this.value = VectorValue.ofUnsigned(width, 0);
		this.nextValue = value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public VectorValue getValue() {
		return value;
	}

	public void setNextValue(VectorValue nextValue) {
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
