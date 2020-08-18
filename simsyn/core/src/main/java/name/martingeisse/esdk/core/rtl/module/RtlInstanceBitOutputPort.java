/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlInstanceBitOutputPort extends RtlInstanceOutputPort implements RtlBitSignal {

	private RtlBitSignal simulationSignal;

	public RtlInstanceBitOutputPort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
		this.simulationSignal = new RtlBitConstant(getRealm(), false);
	}

	@Override
	public RtlBitSignal getSimulationSignal() {
		return simulationSignal;
	}

	public void setSimulationSignal(RtlBitSignal simulationSignal) {
		this.simulationSignal = simulationSignal;
	}

	@Override
	public boolean getValue() {
		return simulationSignal.getValue();
	}

}
