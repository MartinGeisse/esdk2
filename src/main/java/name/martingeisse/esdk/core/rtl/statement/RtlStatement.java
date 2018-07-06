/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlStatement extends RtlItem {

	public RtlStatement(RtlDomain domain) {
		super(domain);
	}

	public abstract void printExpressionsDryRun(VerilogExpressionWriter expressionWriter);

	public abstract void printVerilogStatements(VerilogWriter out);

	public abstract void execute();

}
