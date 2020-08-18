/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlBitAssignmentTarget;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
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

	public final void addStatement(RtlStatement statement) {
		statements.add(checkSameRealm(statement));
	}

	public ImmutableList<RtlStatement> getStatements() {
		return ImmutableList.copyOf(statements);
	}

	@Override
	public boolean isEffectivelyNop() {
		for (RtlStatement statement : statements) {
			if (!statement.isEffectivelyNop()) {
				return false;
			}
		}
		return true;
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
		RtlVectorConstant constant = RtlVectorConstant.of(getRealm(), destination.getWidth(), value);
		RtlVectorAssignment assignment = new RtlVectorAssignment(getRealm(), destination, constant);
		addStatement(assignment);
		return assignment;
	}

	public final RtlWhenStatement when(RtlBitSignal condition) {
		RtlWhenStatement whenStatement = new RtlWhenStatement(getRealm(), condition);
		addStatement(whenStatement);
		return whenStatement;
	}

	public final RtlSwitchStatement switchOn(RtlVectorSignal selector) {
		RtlSwitchStatement switchStatement = new RtlSwitchStatement(getRealm(), selector);
		addStatement(switchStatement);
		return switchStatement;
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
