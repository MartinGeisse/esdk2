/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.statement;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RtlStatementSequence extends RtlStatement {

	private final List<RtlStatement> statements = new ArrayList<>();

	public RtlStatementSequence(RtlDesign design) {
		super(design);
	}

	public boolean isEmpty() {
		return statements.isEmpty();
	}

	public final void addStatement(RtlStatement statement) {
		checkSameDesign(statement);
		statements.add(statement);
	}

	public final RtlBitAssignment assign(RtlBitAssignmentTarget destination, RtlBitSignal source) {
		RtlBitAssignment assignment = new RtlBitAssignment(getDesign(), destination, source);
		addStatement(assignment);
		return assignment;
	}

	public final RtlBitAssignment assignBit(RtlBitAssignmentTarget destination, boolean value) {
		RtlBitConstant constant = new RtlBitConstant(getDesign(), value);
		RtlBitAssignment assignment = new RtlBitAssignment(getDesign(), destination, constant);
		addStatement(assignment);
		return assignment;
	}

	public final RtlVectorAssignment assign(RtlVectorAssignmentTarget destination, RtlVectorSignal source) {
		RtlVectorAssignment assignment = new RtlVectorAssignment(getDesign(), destination, source);
		addStatement(assignment);
		return assignment;
	}

	public final RtlVectorAssignment assignUnsigned(RtlVectorAssignmentTarget destination, int value) {
		if (value < 0) {
			throw new IllegalArgumentException("assignUnsigned called with negative value: " + value);
		}
		RtlVectorConstant constant = RtlVectorConstant.from(getDesign(), destination.getWidth(), value);
		RtlVectorAssignment assignment = new RtlVectorAssignment(getDesign(), destination, constant);
		addStatement(assignment);
		return assignment;
	}

	public final RtlWhenStatement when(RtlBitSignal condition) {
		RtlWhenStatement whenStatement = new RtlWhenStatement(getDesign(), condition);
		addStatement(whenStatement);
		return whenStatement;
	}

	@Override
	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
		for (RtlStatement statement : statements) {
			statement.printExpressionsDryRun(expressionWriter);
		}
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		for (RtlStatement statement : statements) {
			statement.printVerilogStatements(out);
		}
	}

}
