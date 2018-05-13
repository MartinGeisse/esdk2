/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import name.martingeisse.esdk.rtl.statement.RtlStatementSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements an asynchronous or clocked block of statements, equivalent to an "initial" or "always"
 * block in Verilog. For a sequence of statements grouped as a single statement, commonly referred to as a "block"
 * inside compilers, see {@link RtlStatementSequence}.
 */
public abstract class RtlBlock extends RtlItem {

	private final List<RtlProceduralSignal> proceduralSignals = new ArrayList<>();
	private final RtlStatementSequence statements;

	public RtlBlock(RtlDesign design) {
		super(design);
		design.registerBlock(this);
		statements = new RtlStatementSequence(design);
	}

	void registerProceduralSignal(RtlProceduralSignal proceduralSignal) {
		proceduralSignals.add(proceduralSignal);
	}

	public RtlStatementSequence getStatements() {
		return statements;
	}

}
