/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a clocked block of statements, equivalent to a pair of "initial" and "always"
 * blocks in Verilog. The "always" part is triggered by a clock network.
 * <p>
 * For a sequence of statements grouped as a single statement, commonly referred to as a "block"
 * inside compilers, see {@link RtlStatementSequence}.
 */
public final class RtlClockedBlock extends RtlClockedItem {

	private final List<RtlProceduralSignal> proceduralSignals;
	private final RtlStatementSequence initializerStatements;
	private final RtlStatementSequence statements;

	public RtlClockedBlock(RtlClockNetwork clockNetwork) {
		super(clockNetwork);

		this.proceduralSignals = new ArrayList<>();
		this.initializerStatements = new RtlStatementSequence(getRealm());
		this.statements = new RtlStatementSequence(getRealm());
	}


	void registerProceduralSignal(RtlProceduralSignal proceduralSignal) {
		proceduralSignals.add(proceduralSignal);
	}

	public Iterable<RtlProceduralSignal> getProceduralSignals() {
		return proceduralSignals;
	}

	public RtlStatementSequence getInitializerStatements() {
		return initializerStatements;
	}

	public RtlStatementSequence getStatements() {
		return statements;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public RtlProceduralBitSignal createBit() {
		return new RtlProceduralBitSignal(getRealm(), this);
	}

	public RtlProceduralVectorSignal createVector(int width) {
		return new RtlProceduralVectorSignal(getRealm(), this, width);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	public void initializeSimulation() {
		initializerStatements.execute();
		updateState();
	}

	public void computeNextState() {
		statements.execute();
	}

	public void updateState() {
		for (RtlProceduralSignal signal : proceduralSignals) {
			signal.updateValue();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
		initializerStatements.printExpressionsDryRun(expressionWriter);
		statements.printExpressionsDryRun(expressionWriter);
	}

	@Override
	public void printImplementation(VerilogWriter out) {

		out.startProceduralInitialBlock();
		initializerStatements.printVerilogStatements(out);
		out.endProceduralAlwaysBlock();

		out.startProceduralAlwaysBlock("posedge " + out.getSignalName(getClockNetwork().getClockSignal()));
		getStatements().printVerilogStatements(out);
		out.endProceduralAlwaysBlock();

	}

	@Override
	public Iterable<RtlProceduralSignal> getSignalsThatMustBeDeclaredInVerilog() {
		return proceduralSignals;
	}

}
