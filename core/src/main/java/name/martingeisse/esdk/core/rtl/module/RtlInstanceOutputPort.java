/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.module;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlInstanceOutputPort extends RtlInstancePort implements RtlSignal {

	public RtlInstanceOutputPort(RtlModuleInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
	}

	public abstract RtlSignal getSimulationSignal();

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// Instance output ports don't use other signals during synthesis -- the simulation signal gets ignored for that.
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write an implementation expression for instance output ports");
	}

	@Override
	protected void printPortAssignment(VerilogWriter out) {
		out.print("." + getPortName() + "(");
		out.printSignal(this);
		out.print(')');
	}

}
