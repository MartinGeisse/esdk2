/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlRealm;
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

	public RtlProceduralSignal(RtlRealm realm, RtlClockedBlock block) {
		super(realm);
		checkSameRealm(block);
		this.block = block;
		block.registerProceduralSignal(this);
	}

	public RtlClockedBlock getBlock() {
		return block;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the value from the stored next value. Returns true if the value actually changed.
	 */
	public abstract boolean updateValue();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public final void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printProceduralSignalName(this);
	}

	@Override
	public final void printVerilogExpression(VerilogExpressionWriter out) {
		out.printProceduralSignalName(this);
	}

}
