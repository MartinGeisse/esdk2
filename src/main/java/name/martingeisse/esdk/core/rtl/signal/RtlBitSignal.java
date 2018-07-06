/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

/**
 *
 */
public interface RtlBitSignal extends RtlSignal {

	boolean getValue();

	default RtlBitNotOperation not() {
		return new RtlBitNotOperation(getRtlItem().getDomain(), this);
	}

	default RtlBitOperation and(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDomain(), RtlBitOperation.Operator.AND, this, other);
	}

	default RtlBitOperation or(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDomain(), RtlBitOperation.Operator.OR, this, other);
	}

	default RtlBitOperation xor(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDomain(), RtlBitOperation.Operator.XOR, this, other);
	}

	default RtlBitOperation xnor(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDomain(), RtlBitOperation.Operator.XNOR, this, other);
	}

	default RtlConditionalBitOperation conditional(RtlBitSignal onTrue, RtlBitSignal onFalse) {
		return new RtlConditionalBitOperation(getRtlItem().getDomain(), this, onTrue, onFalse);
	}

	default RtlConditionalVectorOperation conditional(RtlVectorSignal onTrue, RtlVectorSignal onFalse) {
		return new RtlConditionalVectorOperation(getRtlItem().getDomain(), this, onTrue, onFalse);
	}

}
