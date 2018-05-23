/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlConditionalVectorOperation extends RtlItem implements RtlVectorSignal {

	private final RtlBitSignal condition;
	private final RtlVectorSignal onTrue;
	private final RtlVectorSignal onFalse;

	public RtlConditionalVectorOperation(RtlDesign design, RtlBitSignal condition, RtlVectorSignal onTrue, RtlVectorSignal onFalse) {
		super(design);
		if (onTrue.getWidth() != onFalse.getWidth()) {
			throw new IllegalArgumentException("onTrue has width " + onTrue.getWidth() + " but onFalse has width " + onFalse.getWidth());
		}
		this.condition = condition;
		this.onTrue = onTrue;
		this.onFalse = onFalse;
	}

	public RtlBitSignal getCondition() {
		return condition;
	}

	public RtlVectorSignal getOnTrue() {
		return onTrue;
	}

	public RtlVectorSignal getOnFalse() {
		return onFalse;
	}
	
}
