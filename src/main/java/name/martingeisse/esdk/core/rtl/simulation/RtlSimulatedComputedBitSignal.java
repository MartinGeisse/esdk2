/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.util.BitProvider;

/**
 * Bit version of {@link RtlSimulatedComputedSignal}.
 */
public abstract class RtlSimulatedComputedBitSignal extends RtlSimulatedComputedSignal implements RtlBitSignal {

	public RtlSimulatedComputedBitSignal(RtlRealm realm) {
		super(realm);
	}

	public static RtlSimulatedComputedBitSignal of(RtlRealm realm, BitProvider bitProvider) {
		return new RtlSimulatedComputedBitSignal(realm) {
			@Override
			public boolean getValue() {
				return bitProvider.getValue();
			}
		};
	}

}
