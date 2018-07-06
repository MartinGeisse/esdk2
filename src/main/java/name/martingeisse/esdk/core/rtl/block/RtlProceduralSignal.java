/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.statement.RtlAssignmentTarget;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlProceduralSignal extends RtlItem implements RtlSignal, RtlAssignmentTarget {

	private final RtlClockedBlock block;

	public RtlProceduralSignal(RtlDomain design, RtlClockedBlock block) {
		super(design);
		checkSameDesign(block);
		this.block = block;
		block.registerProceduralSignal(this);
	}

	public RtlClockedBlock getBlock() {
		return block;
	}

	@Override
	public final void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printProceduralSignalName(this);
	}

	@Override
	public final void printVerilogExpression(VerilogExpressionWriter out) {
		out.printProceduralSignalName(this);
	}

	/**
	 * Updates the value from the stored next value. Returns true if the value actually changed.
	 */
	public abstract boolean updateValue();

}
