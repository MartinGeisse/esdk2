/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.connector;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorSignalConnector extends RtlSignalConnector implements RtlVectorSignal {

	private final int width;
	private RtlVectorSignal connected;

	public RtlVectorSignalConnector(RtlRealm realm, int width) {
		super(realm);
		this.width = width;
	}

	@Override
	public RtlVectorSignal getConnected() {
		return connected;
	}

	public void setConnected(RtlVectorSignal connected) {
		if (connected.getWidth() != width) {
			throw new IllegalArgumentException("wrong signal width for connected signal: " +
				connected.getWidth() + " (should be " + width + ")");
		}
		this.connected = connected;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public VectorValue getValue() {
		return connected.getValue();
	}

}
