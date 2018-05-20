/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlBitSignal;
import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.VerilogWriter;

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

	public final void addStatement(RtlStatement statement) {
		checkSameDesign(statement);
		statements.add(statement);
	}

	public final RtlWhenStatement when(RtlBitSignal condition) {
		RtlWhenStatement whenStatement = new RtlWhenStatement(getDesign(), condition);
		addStatement(whenStatement);
		return whenStatement;
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		for (RtlStatement statement : statements) {
			statement.printVerilogStatements(out);
		}
	}

}
