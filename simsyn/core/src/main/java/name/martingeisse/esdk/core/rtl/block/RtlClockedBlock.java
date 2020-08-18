/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
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
 */
public final class RtlClockedBlock extends RtlClockedItem {

	private final List<RtlProceduralRegister> proceduralRegisters;
	private final List<RtlProceduralMemory> proceduralMemories;
	private final RtlStatementSequence statements;

	public RtlClockedBlock(RtlClockNetwork clockNetwork) {
		super(clockNetwork);

		this.proceduralRegisters = new ArrayList<>();
		this.proceduralMemories = new ArrayList<>();
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
		return new RtlProceduralBitRegister(getRealm(), this, initialValue);
	}

	public RtlProceduralVectorRegister createVector(int width) {
		return new RtlProceduralVectorRegister(getRealm(), this, width);
	}

	public RtlProceduralVectorRegister createVector(int width, VectorValue initialValue) {
		return new RtlProceduralVectorRegister(getRealm(), this, width, initialValue);
	}

	public RtlProceduralVectorRegister createVector(int width, int initialValue) {
		return createVector(width, VectorValue.of(width, initialValue));
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
					context.declareSignal(signal, VerilogSignalDeclarationKeyword.REG, false);
				}
				for (RtlProceduralMemory memory : proceduralMemories) {
					String memoryName = context.declareProceduralMemory(memory);
					VerilogUtil.generateMif(context.getAuxiliaryFileFactory(), memoryName + ".mif", memory.getMatrix());
					memoryNames.put(memory, memoryName);
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
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
				for (RtlProceduralRegister register : proceduralRegisters) {
					out.indent();
					register.printVerilogAssignmentTarget(out);
					out.print(" <= ");
					if (register instanceof RtlProceduralBitRegister) {
						boolean value = ((RtlProceduralBitRegister) register).getValue();
						out.print(RtlBitConstant.getVerilogConstant(value));
					} else if (register instanceof RtlProceduralVectorRegister) {
						VectorValue value = ((RtlProceduralVectorRegister) register).getValue();
						value.printVerilogExpression(out);
					} else {
						throw new RuntimeException("unknown register type: " + register);
					}
					out.println(";");
				}
				for (RtlProceduralMemory memory : proceduralMemories) {
					String memoryName = memoryNames.get(memory);
					Matrix matrix = memory.getMatrix();
					out.println("\t$readmemh(\"" + memoryName + ".mif\", " + memoryName + ", 0, " +
						(matrix.getRowCount() - 1) + ");");
				}
				out.endIndentation();
				out.indent();
				out.println("end");

				out.indent();
				out.print("always @(posedge ");
				out.printSignal(getClockNetwork().getClockSignal());
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
