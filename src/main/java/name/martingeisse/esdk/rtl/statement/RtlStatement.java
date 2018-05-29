/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlItem;
import name.martingeisse.esdk.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.rtl.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlStatement extends RtlItem {

	public RtlStatement(RtlDesign design) {
		super(design);
	}

	public abstract void printExpressionsDryRun(VerilogExpressionWriter expressionWriter);

	public abstract void printVerilogStatements(VerilogWriter out);

}
