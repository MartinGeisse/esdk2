/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlBitSignal;
import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.VerilogWriter;

/**
 *
 */
public final class RtlWhenStatement extends RtlStatement {

	private final RtlBitSignal condition;
	private final RtlStatementSequence thenBranch;
	private final RtlStatementSequence otherwiseBranch;

	public RtlWhenStatement(RtlDesign design, RtlBitSignal condition) {
		super(design);
		this.condition = condition;
		this.thenBranch = new RtlStatementSequence(design);
		this.otherwiseBranch = new RtlStatementSequence(design);
	}

	public RtlStatementSequence getThenBranch() {
		return thenBranch;
	}

	public RtlStatementSequence getOtherwiseBranch() {
		return otherwiseBranch;
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		out.getOut().print("if (");
		out.printExpression(condition);
		out.getOut().println(") begin");
		out.startIndentation();
		thenBranch.printVerilogStatements(out);
		if (otherwiseBranch != null) {
			out.indent();
			out.getOut().println("end else begin");
			otherwiseBranch.printVerilogStatements(out);
		}
		out.endIndentation();
		out.indent();
		out.getOut().println("end");
	}

}
