/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

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

	public List<RtlProceduralSignal> getProceduralSignals() {
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

	public RtlProceduralBitSignal createBit(boolean initialValue) {
		RtlProceduralBitSignal signal = createBit();
		initializerStatements.assign(signal, initialValue);
		return signal;
	}

	public RtlProceduralVectorSignal createVector(int width) {
		return new RtlProceduralVectorSignal(getRealm(), this, width);
	}

	public RtlProceduralVectorSignal createVector(int width, VectorValue initialValue) {
		RtlProceduralVectorSignal signal = createVector(width);
		initializerStatements.assign(signal, initialValue);
		return signal;
	}

	public RtlProceduralMemory createMemory(int rowCount, int columnCount) {
		return new RtlProceduralMemory(this, rowCount, columnCount);
	}

	public RtlProceduralMemory createMemory(Matrix matrix) {
		return new RtlProceduralMemory(this, matrix);
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
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				for (RtlSignal signal : proceduralSignals) {
					context.declareSignal(signal, "r", true, VerilogSignalKind.REG, false);
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				initializerStatements.analyzeSignalUsage(consumer);
				statements.analyzeSignalUsage(consumer);
			}

			@Override
			public void printImplementation(VerilogWriter out) {

				out.indent();
				out.println("initial begin");
				out.startIndentation();
				initializerStatements.printVerilogStatements(out);
				out.endIndentation();
				out.indent();
				out.println("end");

				out.indent();
				out.print("always @(posedge ");
				out.print(getClockNetwork().getClockSignal());
				out.println(") begin");
				out.startIndentation();
				statements.printVerilogStatements(out);
				out.endIndentation();
				out.indent();
				out.println("end");

			}

		};
	}

}
