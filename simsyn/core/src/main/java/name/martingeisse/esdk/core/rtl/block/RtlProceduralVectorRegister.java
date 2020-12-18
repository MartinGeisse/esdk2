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
		setInitialized(true);
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

	/**
	 * This method directly sets the current value. This is useful, for example, to override the initial value of
	 * a register for simulation.
	 *
	 * DO NOT CALL THIS from within any clock handler! Doing so makes the behavior dependent on the order in which
	 * clock handlers are executed, which is undefined by design.
	 */
	public void overrideCurrentValue(VectorValue value) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("trying to set next value of wrong width " + value.getWidth() + ", should be " + width);
		}
		this.value = value;
		// We also have to override the next value because for a procedural register that gets assigned inside an
		// if-statement (i.e. has a clock enable), that statement conditionally sets the next value, but the next
		// value always gets written to the current value in updateValue(), restoring the old value from before this
		// method got called.
		this.nextValue = value;
	}

}
