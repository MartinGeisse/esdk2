/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlInstanceVectorOutputPort extends RtlInstanceOutputPort implements RtlVectorSignal {

	private RtlVectorSignal simulationSignal;

	public RtlInstanceVectorOutputPort(RtlModuleInstance moduleInstance, String portName, int width) {
		super(moduleInstance, portName);
		this.simulationSignal = new RtlVectorConstant(getRealm(), VectorValue.of(width, 0));
	}

	@Override
	public RtlVectorSignal getSimulationSignal() {
		return simulationSignal;
	}

	public void setSimulationSignal(RtlVectorSignal simulationSignal) {
		this.simulationSignal = simulationSignal;
	}

	@Override
	public int getWidth() {
		return simulationSignal.getWidth();
	}

	@Override
	public VectorValue getValue() {
		return simulationSignal.getValue();
	}

}
