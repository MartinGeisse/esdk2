/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.IntProvider;
import name.martingeisse.esdk.core.util.VectorProvider;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Vector version of {@link RtlSimulatedComputedSignal}.
 */
public abstract class RtlSimulatedComputedVectorSignal extends RtlSimulatedComputedSignal implements RtlVectorSignal {

	public RtlSimulatedComputedVectorSignal(RtlRealm realm) {
		super(realm);
	}

	public static RtlSimulatedComputedVectorSignal of(RtlRealm realm, int width, VectorProvider vectorProvider) {
		return new RtlSimulatedComputedVectorSignal(realm) {

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

	public static RtlSimulatedComputedVectorSignal ofUnsigned(RtlRealm realm, int width, IntProvider intProvider) {
		return new RtlSimulatedComputedVectorSignal(realm) {

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
