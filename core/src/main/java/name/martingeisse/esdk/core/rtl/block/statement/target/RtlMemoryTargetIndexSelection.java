/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlMemoryTargetIndexSelection extends RtlItem implements RtlVectorAssignmentTarget {

	private final RtlProceduralMemory memory;
	private final RtlVectorSignal indexSignal;

	public RtlMemoryTargetIndexSelection(RtlRealm realm, RtlProceduralMemory memory, RtlVectorSignal indexSignal) {
		super(realm);
		int rowCount = memory.getMatrix().getRowCount();
		if (rowCount < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("memory with " + rowCount + " rows is too small for index of width " + indexSignal.getWidth());
		}
		this.memory = checkSameRealm(memory);
		this.indexSignal = checkSameRealm(indexSignal);
	}

	public RtlProceduralMemory getMemory() {
		return memory;
	}

	public RtlVectorSignal getIndexSignal() {
		return indexSignal;
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
		int index = indexSignal.getValue().getAsUnsignedInt();
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
		out.print(memory);
		out.print('[');
		out.print(indexSignal);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

}
