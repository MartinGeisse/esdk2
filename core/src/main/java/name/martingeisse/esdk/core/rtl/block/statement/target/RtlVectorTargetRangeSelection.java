/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorRegister;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorTargetRangeSelection extends RtlItem implements RtlVectorAssignmentTarget {

	private final RtlProceduralVectorRegister containerTarget;
	private final int from;
	private final int to;

	public RtlVectorTargetRangeSelection(RtlRealm realm, RtlProceduralVectorRegister containerTarget, int from, int to) {
		super(realm);
		if (from < 0 || to < 0 || from >= containerTarget.getWidth() || to >= containerTarget.getWidth() || from < to) {
			throw new IllegalArgumentException("invalid from/to indices for container width " +
				containerTarget.getWidth() + ": from = " + from + ", to = " + to);
		}
		this.containerTarget = checkSameRealm(containerTarget);
		this.from = from;
		this.to = to;
	}

	public RtlProceduralVectorRegister getContainerTarget() {
		return containerTarget;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int getWidth() {
		return from - to + 1;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void setNextValue(VectorValue nextValue) {
		VectorValue nextContainerValue = containerTarget.getNextValue();
		VectorValue updatedValue;
		if (to == 0) {
			if (from == nextContainerValue.getWidth() - 1) {
				updatedValue = nextValue;
			} else {
				VectorValue upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, from + 1);
				updatedValue = upper.concat(nextValue);
			}
		} else {
			if (from == nextContainerValue.getWidth() - 1) {
				VectorValue lower = nextContainerValue.select(to - 1, 0);
				updatedValue = nextValue.concat(lower);
			} else {
				VectorValue upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, from + 1);
				VectorValue lower = nextContainerValue.select(to - 1, 0);
				updatedValue = upper.concat(nextValue).concat(lower);
			}
		}
		containerTarget.setNextValue(updatedValue);
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
		containerTarget.printVerilogAssignmentTarget(out);
		out.print('[');
		out.print(from);
		out.print(':');
		out.print(to);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		containerTarget.analyzeSignalUsage(consumer);
	}

}
