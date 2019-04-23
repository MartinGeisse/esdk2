/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;

/**
 *
 */
public abstract class RtlProceduralSignal extends RtlItem implements RtlSignal, RtlAssignmentTarget {

	private final RtlClockedBlock block;

	public RtlProceduralSignal(RtlRealm realm, RtlClockedBlock block) {
		super(realm);
		this.block = checkSameRealm(block);
		block.registerProceduralSignal(this);
	}

	public RtlClockedBlock getBlock() {
		return block;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the value from the stored next value.
	 */
	abstract void updateValue();

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
		out.print(this);
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// procedural signals themselves don't use other signals; the assignments that
		// assign values to them do.
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write an implementation expression for procedural signals");
	}

}
