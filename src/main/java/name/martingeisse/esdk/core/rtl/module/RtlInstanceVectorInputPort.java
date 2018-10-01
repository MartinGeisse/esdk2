/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;

/**
 *
 */
public final class RtlInstanceVectorInputPort extends RtlInstanceInputPort {

	private final RtlVectorSignal assignedSignal;

	public RtlInstanceVectorInputPort(RtlModuleInstance moduleInstance, String portName, RtlVectorSignal assignedSignal) {
		super(moduleInstance, portName);
		this.assignedSignal = assignedSignal;
	}

	@Override
	public RtlVectorSignal getAssignedSignal() {
		return assignedSignal;
	}

}
