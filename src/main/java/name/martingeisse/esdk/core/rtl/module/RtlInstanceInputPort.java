/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public abstract class RtlInstanceInputPort extends RtlInstancePort {

	public RtlInstanceInputPort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
	}

	public abstract RtlSignal getAssignedSignal();

}
