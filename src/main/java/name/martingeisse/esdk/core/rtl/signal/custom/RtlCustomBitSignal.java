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
 * Helper class that extends both {@link RtlItem} and {@link RtlBitSignal} and the same time to implement custom
 * bit-typed signals. (You cannot create an anonymous class that extends both at the same time, but now you can
 * extend this class).
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
