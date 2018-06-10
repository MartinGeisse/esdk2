/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;

/**
 *
 */
public final class RtlConditionalBitOperation extends RtlConditionalOperation implements RtlBitSignal {

	private final RtlBitSignal onTrue;
	private final RtlBitSignal onFalse;

	public RtlConditionalBitOperation(RtlDesign design, RtlBitSignal condition, RtlBitSignal onTrue, RtlBitSignal onFalse) {
		super(design, condition);
		this.onTrue = onTrue;
		this.onFalse = onFalse;
	}

	public RtlBitSignal getOnTrue() {
		return onTrue;
	}

	public RtlBitSignal getOnFalse() {
		return onFalse;
	}

}
