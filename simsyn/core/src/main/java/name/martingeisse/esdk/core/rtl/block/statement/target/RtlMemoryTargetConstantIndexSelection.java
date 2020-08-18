/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlMemoryTargetConstantIndexSelection extends RtlItem implements RtlVectorAssignmentTarget {

	private final RtlProceduralMemory memory;
	private final int index;

	public RtlMemoryTargetConstantIndexSelection(RtlRealm realm, RtlProceduralMemory memory, int index) {
		super(realm);
		if (index < 0 || index >= memory.getMatrix().getRowCount()) {
			throw new IllegalArgumentException("index " + index + " is out of range for matrix row count " + memory.getMatrix().getRowCount());
		}
		this.memory = checkSameRealm(memory);
		this.index = index;
	}

	public RtlProceduralMemory getMemory() {
		return memory;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getWidth() {
		return memory.getMatrix().getColumnCount();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void setNextValue(VectorValue nextValue) {
		memory.requestUpdate(index, nextValue);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		// procedural signals are synthesized as part of the block that defines them
		return new EmptyVerilogContribution();
	}

	@Override
	public final void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printMemory(memory);
		out.print('[');
		out.print(index);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
	}

}
