/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

/**
 *
 */
public interface RtlAssignmentTarget extends RtlItemOwned {

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Writes a Verilog assignment target for this assignment target.
	 */
	void printVerilogAssignmentTarget(VerilogWriter out);

}
