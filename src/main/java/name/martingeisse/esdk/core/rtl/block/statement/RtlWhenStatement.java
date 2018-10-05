/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public final class RtlWhenStatement extends RtlStatement {

	private final RtlBitSignal condition;
	private final RtlStatementSequence thenBranch;
	private final RtlStatementSequence otherwiseBranch;

	public RtlWhenStatement(RtlRealm realm, RtlBitSignal condition) {
		super(realm);
		this.condition = condition;
		this.thenBranch = new RtlStatementSequence(realm);
		this.otherwiseBranch = new RtlStatementSequence(realm);
	}

	public RtlStatementSequence getThenBranch() {
		return thenBranch;
	}

	public RtlStatementSequence getOtherwiseBranch() {
		return otherwiseBranch;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
		if (condition.getValue()) {
			thenBranch.execute();
		} else {
			otherwiseBranch.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
		expressionWriter.print(condition, VerilogExpressionNesting.ALL);
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

}
