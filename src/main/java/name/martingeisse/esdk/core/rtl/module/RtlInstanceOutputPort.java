/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSettableSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogSignalKind;

/**
 *
 */
public abstract class RtlInstanceOutputPort extends RtlInstancePort implements RtlSignal {

	public RtlInstanceOutputPort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
	}

	public abstract RtlSettableSignal getSettableSignal();

	@Override
	public final void printVerilogExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write an implementation expression for instance output ports");
	}

}
