/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.custom;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.IntProvider;
import name.martingeisse.esdk.core.util.VectorProvider;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Helper class that extends both {@link RtlItem} and {@link RtlVectorSignal} and the same time to implement custom
 * vector-typed signals. (You cannot create an anonymous class that extends both at the same time, but now you can
 * extend this class).
 */
public abstract class RtlCustomVectorSignal extends RtlCustomSignal implements RtlVectorSignal {

	public RtlCustomVectorSignal(RtlRealm realm) {
		super(realm);
	}

	public static RtlCustomVectorSignal of(RtlRealm realm, int width, VectorProvider vectorProvider) {
		return new RtlCustomVectorSignal(realm) {

			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public VectorValue getValue() {
				return vectorProvider.getValue();
			}

		};
	}

	public static RtlCustomVectorSignal ofUnsigned(RtlRealm realm, int width, IntProvider intProvider) {
		return new RtlCustomVectorSignal(realm) {

			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public VectorValue getValue() {
				return VectorValue.ofUnsigned(width, intProvider.getValue());
			}

		};
	}

}
