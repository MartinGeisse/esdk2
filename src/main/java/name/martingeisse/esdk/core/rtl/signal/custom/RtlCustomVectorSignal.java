/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.custom;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 * Helper class that extends both {@link RtlItem} and {@link RtlVectorSignal} and the same time. An anonymous signal
 * class couldn't do this otherwise.
 */
public abstract class RtlCustomVectorSignal extends RtlCustomSignal implements RtlVectorSignal {

	public RtlCustomVectorSignal(RtlRealm realm) {
		super(realm);
	}

}
