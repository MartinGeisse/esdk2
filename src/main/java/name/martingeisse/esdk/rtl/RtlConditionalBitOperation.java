/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlConditionalBitOperation extends RtlItem implements RtlBitSignal {

	private final RtlBitSignal condition;
	private final RtlBitSignal onTrue;
	private final RtlBitSignal onFalse;

	public RtlConditionalBitOperation(RtlDesign design, RtlBitSignal condition, RtlBitSignal onTrue, RtlBitSignal onFalse) {
		super(design);
		this.condition = condition;
		this.onTrue = onTrue;
		this.onFalse = onFalse;
	}

	public RtlBitSignal getCondition() {
		return condition;
	}

	public RtlBitSignal getOnTrue() {
		return onTrue;
	}

	public RtlBitSignal getOnFalse() {
		return onFalse;
	}

}
