/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlProceduralMemoryConstantIndexSelection extends RtlItem implements RtlVectorSignal {

	private final RtlProceduralMemory memory;
	private final int index;

	public RtlProceduralMemoryConstantIndexSelection(RtlRealm realm, RtlProceduralMemory memory, int index) {
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
	public VectorValue getValue() {
		return memory.getMatrix().getRow(index);
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
		out.print(index);
		out.print(']');
	}

}
