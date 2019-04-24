/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlProceduralMemoryIndexSelection extends RtlItem implements RtlVectorSignal {

	private final RtlProceduralMemory memory;
	private final RtlVectorSignal indexSignal;

	public RtlProceduralMemoryIndexSelection(RtlRealm realm, RtlProceduralMemory memory, RtlVectorSignal indexSignal) {
		super(realm);
		if (memory.getMatrix().getRowCount() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("memory with " + memory.getMatrix().getRowCount() +
				" rows is too small for index of width " + indexSignal.getWidth());
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
	public VectorValue getValue() {
		return memory.getMatrix().getRow(indexSignal.getValue().getAsUnsignedInt());
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting != VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(memory);
		out.print('[');
		out.print(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print(']');
	}

}
