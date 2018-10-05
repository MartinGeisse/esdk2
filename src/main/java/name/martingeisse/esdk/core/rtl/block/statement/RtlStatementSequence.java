/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RtlStatementSequence extends RtlStatement {

	private final List<RtlStatement> statements = new ArrayList<>();

	public RtlStatementSequence(RtlRealm realm) {
		super(realm);
	}

	public boolean isEmpty() {
		return statements.isEmpty();
	}

	public final void addStatement(RtlStatement statement) {
		statements.add(checkSameRealm(statement));
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public RtlStatementBuilder builder() {
		return new RtlStatementBuilder(this);
	}

	public final RtlBitAssignment assign(RtlBitAssignmentTarget destination, RtlBitSignal source) {
		RtlBitAssignment assignment = new RtlBitAssignment(getRealm(), destination, source);
		addStatement(assignment);
		return assignment;
	}

	public final RtlBitAssignment assign(RtlBitAssignmentTarget destination, boolean value) {
		return assign(destination, new RtlBitConstant(getRealm(), value));
	}

	public final RtlVectorAssignment assign(RtlVectorAssignmentTarget destination, RtlVectorSignal source) {
		RtlVectorAssignment assignment = new RtlVectorAssignment(getRealm(), destination, source);
		addStatement(assignment);
		return assignment;
	}

	public final RtlVectorAssignment assign(RtlVectorAssignmentTarget destination, VectorValue value) {
		return assign(destination, new RtlVectorConstant(getRealm(), value));
	}

	public final RtlVectorAssignment assignUnsigned(RtlVectorAssignmentTarget destination, int value) {
		if (value < 0) {
			throw new IllegalArgumentException("assignUnsigned called with negative value: " + value);
		}
		RtlVectorConstant constant = RtlVectorConstant.ofUnsigned(getRealm(), destination.getWidth(), value);
		RtlVectorAssignment assignment = new RtlVectorAssignment(getRealm(), destination, constant);
		addStatement(assignment);
		return assignment;
	}

	public final RtlWhenStatement when(RtlBitSignal condition) {
		RtlWhenStatement whenStatement = new RtlWhenStatement(getRealm(), condition);
		addStatement(whenStatement);
		return whenStatement;
	}

	public final RtlConditionChain conditionChain() {
		RtlConditionChain chain = new RtlConditionChain(getRealm());
		addStatement(chain);
		return chain;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
		for (RtlStatement statement : statements) {
			statement.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		for (RtlStatement statement : statements) {
			statement.analyzeSignalUsage(consumer);
		}
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		for (RtlStatement statement : statements) {
			statement.printVerilogStatements(out);
		}
	}

}
