/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.getter.DefaultSignalGetterFactory;
import name.martingeisse.esdk.core.rtl.signal.getter.RtlBitSignalGetter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public final class RtlWhenStatement extends RtlStatement {

	private final RtlBitSignal condition;
	private final RtlStatementSequence thenBranch;
	private final RtlStatementSequence otherwiseBranch;
	private RtlBitSignalGetter conditionGetter;

	public RtlWhenStatement(RtlRealm realm, RtlBitSignal condition) {
		super(realm);
		this.condition = condition;
		this.thenBranch = new RtlStatementSequence(realm);
		this.otherwiseBranch = new RtlStatementSequence(realm);
	}

	public RtlBitSignal getCondition() {
		return condition;
	}

	public RtlStatementSequence getThenBranch() {
		return thenBranch;
	}

	public RtlStatementSequence getOtherwiseBranch() {
		return otherwiseBranch;
	}

	@Override
	public boolean isEffectivelyNop() {
		return thenBranch.isEffectivelyNop() && otherwiseBranch.isEffectivelyNop();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------


	@Override
	protected void initializeSimulation() {
		super.initializeSimulation();
		conditionGetter = DefaultSignalGetterFactory.getGetter(condition);
	}

	@Override
	public void execute() {
		if (conditionGetter.getValue()) {
			thenBranch.execute();
		} else {
			otherwiseBranch.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.getFakeExpressionWriter().printSignal(condition, VerilogExpressionNesting.ALL);
		thenBranch.analyzeSignalUsage(consumer);
		otherwiseBranch.analyzeSignalUsage(consumer);
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		out.print("if (");
		out.printSignal(condition);
		out.println(") begin");
		out.startIndentation();
		thenBranch.printVerilogStatements(out);
		if (!otherwiseBranch.isEffectivelyNop()) {
			out.endIndentation();
			out.indent();
			out.println("end else begin");
			out.startIndentation();
			otherwiseBranch.printVerilogStatements(out);
		}
		out.endIndentation();
		out.indent();
		out.println("end");
	}

}
