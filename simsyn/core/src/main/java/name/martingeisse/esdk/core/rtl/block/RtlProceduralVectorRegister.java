/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorAssignmentTarget;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorTargetConstantIndexSelection;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorTargetIndexSelection;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorTargetRangeSelection;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlProceduralVectorRegister extends RtlProceduralRegister implements RtlVectorSignal, RtlVectorAssignmentTarget {

	private final int width;
	private VectorValue value;
	private VectorValue nextValue;

	public RtlProceduralVectorRegister(RtlRealm realm, RtlClockedBlock block, int width) {
		super(realm, block);
		this.width = width;
		this.value = VectorValue.of(width, 0);
		this.nextValue = value;
	}

	public RtlProceduralVectorRegister(RtlRealm realm, RtlClockedBlock block, int width, VectorValue initialValue) {
		super(realm, block);
		if (initialValue.getWidth() != width) {
			throw new IllegalArgumentException("initial value must have width of register (" + width + "): " + initialValue);
		}
		this.width = width;
		this.value = initialValue;
		this.nextValue = value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public RtlVectorTargetIndexSelection selectTarget(RtlVectorSignal index) {
		return new RtlVectorTargetIndexSelection(getRtlItem().getRealm(), this, index);
	}

	public RtlVectorTargetConstantIndexSelection selectTarget(int index) {
		return new RtlVectorTargetConstantIndexSelection(getRtlItem().getRealm(), this, index);
	}

	public RtlVectorTargetRangeSelection selectTarget(int from, int to) {
		return new RtlVectorTargetRangeSelection(getRtlItem().getRealm(), this, from, to);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return value;
	}

	public VectorValue getNextValue() {
		return nextValue;
	}

	@Override
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
	void updateValue() {
		value = nextValue;
	}

}
