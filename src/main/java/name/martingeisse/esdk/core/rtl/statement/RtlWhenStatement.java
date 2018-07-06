/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlRegion;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

/**
 *
 */
public final class RtlWhenStatement extends RtlStatement {

	private final RtlBitSignal condition;
	private final RtlStatementSequence thenBranch;
	private final RtlStatementSequence otherwiseBranch;

	public RtlWhenStatement(RtlRegion region, RtlBitSignal condition) {
		super(region);
		this.condition = condition;
		this.thenBranch = new RtlStatementSequence(region);
		this.otherwiseBranch = new RtlStatementSequence(region);
	}

	public RtlStatementSequence getThenBranch() {
		return thenBranch;
	}

	public RtlStatementSequence getOtherwiseBranch() {
		return otherwiseBranch;
	}

	@Override
	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
		expressionWriter.print(condition, VerilogGenerator.VerilogExpressionNesting.ALL);
		thenBranch.printExpressionsDryRun(expressionWriter);
		otherwiseBranch.printExpressionsDryRun(expressionWriter);
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		out.getOut().print("if (");
		out.printExpression(condition);
		out.getOut().println(") begin");
		out.startIndentation();
		thenBranch.printVerilogStatements(out);
		if (!otherwiseBranch.isEmpty()) {
			out.endIndentation();
			out.indent();
			out.getOut().println("end else begin");
			out.startIndentation();
			otherwiseBranch.printVerilogStatements(out);
		}
		out.endIndentation();
		out.indent();
		out.getOut().println("end");
	}

	@Override
	public void execute() {
		if (condition.getValue()) {
			thenBranch.execute();
		} else {
			otherwiseBranch.execute();
		}
	}

}
