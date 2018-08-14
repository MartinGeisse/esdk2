/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.custom;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.util.BitProvider;

/**
 * Helper class that extends both {@link RtlItem} and {@link RtlBitSignal} and the same time. An anonymous signal
 * class couldn't do this otherwise.
 */
public abstract class RtlCustomBitSignal extends RtlCustomSignal implements RtlBitSignal {

	public RtlCustomBitSignal(RtlRealm realm) {
		super(realm);
	}

	public static RtlCustomBitSignal of(RtlRealm realm, BitProvider bitProvider) {
		return new RtlCustomBitSignal(realm) {
			@Override
			public boolean getValue() {
				return bitProvider.getValue();
			}
		};
	}

}
