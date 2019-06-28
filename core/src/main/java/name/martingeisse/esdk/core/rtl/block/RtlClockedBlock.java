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
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements a clocked block of statements, equivalent to a pair of "initial" and "always"
 * blocks in Verilog. The "always" part is triggered by a clock network.
 * <p>
 * For a sequence of statements grouped as a single statement, commonly referred to as a "block"
 * inside compilers, see {@link RtlStatementSequence}.
 * <p>
 * TODO get rid of "initializer statements" and just use the current value of procedural registers to create the
 * initial-block, just like for matrices.
 * </p>
 */
public final class RtlClockedBlock extends RtlClockedItem {

	private final List<RtlProceduralRegister> proceduralRegisters;
	private final List<RtlProceduralMemory> proceduralMemories;
	private final RtlStatementSequence initializerStatements;
	private final RtlStatementSequence statements;

	public RtlClockedBlock(RtlClockNetwork clockNetwork) {
		super(clockNetwork);

		this.proceduralRegisters = new ArrayList<>();
		this.proceduralMemories = new ArrayList<>();
		this.initializerStatements = new RtlStatementSequence(getRealm());
		this.statements = new RtlStatementSequence(getRealm());
	}

	void registerProceduralRegister(RtlProceduralRegister proceduralRegister) {
		proceduralRegisters.add(proceduralRegister);
	}

	void registerProceduralMemory(RtlProceduralMemory proceduralMemory) {
		proceduralMemories.add(proceduralMemory);
	}

	public List<RtlProceduralRegister> getProceduralRegisters() {
		return proceduralRegisters;
	}

	public List<RtlProceduralMemory> getProceduralMemories() {
		return proceduralMemories;
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

	public RtlProceduralBitRegister createBit() {
		return new RtlProceduralBitRegister(getRealm(), this);
	}

	public RtlProceduralBitRegister createBit(boolean initialValue) {
		RtlProceduralBitRegister register = new RtlProceduralBitRegister(getRealm(), this, initialValue);
		initializerStatements.assign(register, initialValue);
		return register;
	}

	public RtlProceduralVectorRegister createVector(int width) {
		return new RtlProceduralVectorRegister(getRealm(), this, width);
	}

	public RtlProceduralVectorRegister createVector(int width, VectorValue initialValue) {
		RtlProceduralVectorRegister register = new RtlProceduralVectorRegister(getRealm(), this, width, initialValue);
		initializerStatements.assign(register, initialValue);
		return register;
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
		for (RtlProceduralRegister signal : proceduralRegisters) {
			signal.updateValue();
		}
		for (RtlProceduralMemory memory : proceduralMemories) {
			memory.updateMatrix();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			private final Map<RtlProceduralMemory, String> memoryNames = new HashMap<>();

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				for (RtlSignal signal : proceduralRegisters) {
					context.declareSignal(signal, "r", VerilogSignalKind.REG, false);
				}
				for (RtlProceduralMemory memory : proceduralMemories) {
					String memoryName = context.declareProceduralMemory(memory);
					VerilogUtil.generateMif(context.getAuxiliaryFileFactory(), memoryName + ".mif", memory.getMatrix());
					memoryNames.put(memory, memoryName);
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				initializerStatements.analyzeSignalUsage(consumer);
				statements.analyzeSignalUsage(consumer);
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
				for (RtlProceduralMemory memory : proceduralMemories) {
					String memoryName = memoryNames.get(memory);
					Matrix matrix = memory.getMatrix();
					out.println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
						(matrix.getRowCount() - 1) + ":0];");
				}
			}

			@Override
			public void printImplementation(VerilogWriter out) {

				out.indent();
				out.println("initial begin");
				out.startIndentation();
				initializerStatements.printVerilogStatements(out);
				for (RtlProceduralMemory memory : proceduralMemories) {
					String memoryName = memoryNames.get(memory);
					Matrix matrix = memory.getMatrix();
					out.println("\t$readmemh(\"" + memoryName + ".mif\", " + memoryName + ", 0, " +
						(matrix.getRowCount() - 1) + ");\n");
				}
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
