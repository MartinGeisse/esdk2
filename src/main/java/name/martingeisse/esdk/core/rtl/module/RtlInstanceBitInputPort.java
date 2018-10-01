/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlInstanceBitInputPort extends RtlInstanceInputPort {

	private final RtlBitSignal assignedSignal;

	public RtlInstanceBitInputPort(RtlModuleInstance moduleInstance, String portName, RtlBitSignal assignedSignal) {
		super(moduleInstance, portName);
		this.assignedSignal = assignedSignal;
	}

	@Override
	public RtlBitSignal getAssignedSignal() {
		return assignedSignal;
	}

}
