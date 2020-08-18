/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public interface RtlAssignmentTarget extends RtlItemOwned {

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Passes signals used by this assignment target to the specified consumer.
	 */
	void analyzeSignalUsage(SignalUsageConsumer consumer);

	/**
	 * Writes a Verilog assignment target for this assignment target.
	 */
	void printVerilogAssignmentTarget(VerilogWriter out);

}
