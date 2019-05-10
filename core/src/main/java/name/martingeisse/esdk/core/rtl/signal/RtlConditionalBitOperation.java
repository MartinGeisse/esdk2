/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlRealm;

/**
 *
 */
public final class RtlConditionalBitOperation extends RtlConditionalOperation implements RtlBitSignal {

	private final RtlBitSignal onTrue;
	private final RtlBitSignal onFalse;

	public RtlConditionalBitOperation(RtlRealm realm, RtlBitSignal condition, RtlBitSignal onTrue, RtlBitSignal onFalse) {
		super(realm, condition);
		this.onTrue = checkSameRealm(onTrue);
		this.onFalse = checkSameRealm(onFalse);
	}

	public RtlBitSignal getOnTrue() {
		return onTrue;
	}

	public RtlBitSignal getOnFalse() {
		return onFalse;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return getCondition().getValue() ? onTrue.getValue() : onFalse.getValue();
	}

}
