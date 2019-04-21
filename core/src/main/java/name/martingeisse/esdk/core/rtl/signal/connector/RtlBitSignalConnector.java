/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.connector;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 * Bit-typed signal connector. See {@link RtlSignalConnector} for details.
 */
public final class RtlBitSignalConnector extends RtlSignalConnector implements RtlBitSignal {

	private RtlBitSignal connected;

	public RtlBitSignalConnector(RtlRealm realm) {
		super(realm);
	}

	@Override
	public RtlBitSignal getConnected() {
		return connected;
	}

	public void setConnected(RtlBitSignal connected) {
		this.connected = connected;
	}

	@Override
	public boolean getValue() {
		return connected.getValue();
	}

}
