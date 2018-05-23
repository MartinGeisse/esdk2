/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public interface RtlBitSignal extends RtlSignal {

	default RtlBitSignal and(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDesign(), RtlBitOperation.Operator.AND, this, other);
	}

	default RtlBitSignal or(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDesign(), RtlBitOperation.Operator.OR, this, other);
	}

	default RtlBitSignal xor(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDesign(), RtlBitOperation.Operator.XOR, this, other);
	}

	default RtlBitSignal xnor(RtlBitSignal other) {
		return new RtlBitOperation(getRtlItem().getDesign(), RtlBitOperation.Operator.XNOR, this, other);
	}

}
