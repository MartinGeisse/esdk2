/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.RtlItem;

/**
 *
 */
public abstract class RtlInstancePort extends RtlItem {

	private final RtlModuleInstance moduleInstance;
	private final String portName;

	public RtlInstancePort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance.getRealm());
		this.moduleInstance = moduleInstance;
		this.portName = portName;
	}

	public final RtlModuleInstance getModuleInstance() {
		return moduleInstance;
	}

	public final String getPortName() {
		return portName;
	}

}