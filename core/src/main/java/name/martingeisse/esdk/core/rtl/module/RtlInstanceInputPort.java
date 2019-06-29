/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlInstanceInputPort extends RtlInstancePort {

	public RtlInstanceInputPort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
	}

	public abstract RtlSignal getAssignedSignal();

	@Override
	protected void printPortAssignment(VerilogWriter out) {
		out.print("." + getPortName() + "(");
		if (getAssignedSignal() == null) {
			throw new IllegalStateException("input port " + getPortName() +
					" of instance of module " + getModuleInstance().getModuleName() + " has no assigned signal");
		}
		out.printSignal(getAssignedSignal());
		out.print(')');
	}

}
