/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlInstanceVectorOutputPort extends RtlInstanceOutputPort implements RtlVectorSignal {

	private final RtlSettableVectorSignal settableSignal;

	public RtlInstanceVectorOutputPort(RtlModuleInstance moduleInstance, String portName, int width) {
		super(moduleInstance, portName);
		this.settableSignal = new RtlSettableVectorSignal(getRealm(), width);
	}

	@Override
	public RtlSettableSignal getSettableSignal() {
		return settableSignal;
	}

	@Override
	public int getWidth() {
		return settableSignal.getWidth();
	}

	@Override
	public VectorValue getValue() {
		return settableSignal.getValue();
	}

}
