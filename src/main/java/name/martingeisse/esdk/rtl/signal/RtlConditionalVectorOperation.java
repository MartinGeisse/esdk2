/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.signal;

import name.martingeisse.esdk.rtl.RtlDesign;

/**
 *
 */
public final class RtlConditionalVectorOperation extends RtlConditionalOperation implements RtlVectorSignal {

	private final RtlVectorSignal onTrue;
	private final RtlVectorSignal onFalse;

	public RtlConditionalVectorOperation(RtlDesign design, RtlBitSignal condition, RtlVectorSignal onTrue, RtlVectorSignal onFalse) {
		super(design, condition);
		if (onTrue.getWidth() != onFalse.getWidth()) {
			throw new IllegalArgumentException("onTrue has width " + onTrue.getWidth() + " but onFalse has width " + onFalse.getWidth());
		}
		this.onTrue = onTrue;
		this.onFalse = onFalse;
	}

	public RtlVectorSignal getOnTrue() {
		return onTrue;
	}

	public RtlVectorSignal getOnFalse() {
		return onFalse;
	}

	@Override
	public int getWidth() {
		return onTrue.getWidth();
	}

}
