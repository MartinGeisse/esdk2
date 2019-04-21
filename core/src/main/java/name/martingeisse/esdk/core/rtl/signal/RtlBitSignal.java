/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;

/**
 *
 */
public interface RtlBitSignal extends RtlSignal {

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	boolean getValue();

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	default RtlBitNotOperation not() {
		return new RtlBitNotOperation(getRtlItem().getRealm(), this);
	}

	default RtlBitOperation and(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getRealm(), RtlBitOperation.Operator.AND, this, other);
	}

	default RtlBitOperation or(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getRealm(), RtlBitOperation.Operator.OR, this, other);
	}

	default RtlBitOperation xor(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getRealm(), RtlBitOperation.Operator.XOR, this, other);
	}

	default RtlBitOperation xnor(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getRealm(), RtlBitOperation.Operator.XNOR, this, other);
	}

	default RtlConditionalBitOperation conditional(RtlBitSignal onTrue, RtlBitSignal onFalse) {
		return new RtlConditionalBitOperation(getRtlItem().getRealm(), this, onTrue, onFalse);
	}

	default RtlConditionalVectorOperation conditional(RtlVectorSignal onTrue, RtlVectorSignal onFalse) {
		return new RtlConditionalVectorOperation(getRtlItem().getRealm(), this, onTrue, onFalse);
	}

	default RtlOneBitVectorSignal asOneBitVector() {
		return new RtlOneBitVectorSignal(getRtlItem().getRealm(), this);
	}

	default RtlBitRepetition repeat(int repetitions) {
		return new RtlBitRepetition(getRtlItem().getRealm(), this, repetitions);
	}

	default RtlBitSampler sampler(RtlClockNetwork clock) {
		return new RtlBitSampler(clock, this);
	}

}
