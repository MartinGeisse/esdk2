/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import name.martingeisse.esdk.rtl.VerilogExpressionWriter;

/**
 *
 */
public interface RtlAssignmentTarget extends RtlItemOwned {

	/**
	 * Writes a Verilog assignment target for this assignment target.
	 */
	void printVerilogAssignmentTarget(VerilogWriter out);

}
