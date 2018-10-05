/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlAssignment extends RtlStatement {

	public RtlAssignment(RtlRealm realm) {
		super(realm);
	}

	public abstract RtlAssignmentTarget getDestination();
	public abstract RtlSignal getSource();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// TODO what if the destination is a selection with a complex index? Shouldn't this dry-run-print the
		// destination too?
		consumer.consumeSignalUsage(getSource(), VerilogExpressionNesting.ALL);
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		getDestination().printVerilogAssignmentTarget(out);
		out.print(" <= ");
		out.print(getSource());
		out.println(";");
	}

}
