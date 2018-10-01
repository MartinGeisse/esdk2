/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableSignal;

/**
 *
 */
public final class RtlInstanceBitOutputPort extends RtlInstanceOutputPort implements RtlBitSignal {

	private final RtlSettableBitSignal settableSignal;

	public RtlInstanceBitOutputPort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
		this.settableSignal = new RtlSettableBitSignal(getRealm());
	}

	@Override
	public RtlSettableSignal getSettableSignal() {
		return settableSignal;
	}

	@Override
	public boolean getValue() {
		return settableSignal.getValue();
	}

}
