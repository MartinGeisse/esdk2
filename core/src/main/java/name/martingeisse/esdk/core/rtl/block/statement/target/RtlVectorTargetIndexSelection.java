/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement.target;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorTargetIndexSelection extends RtlItem implements RtlBitAssignmentTarget {

	private final RtlVectorAssignmentTarget containerTarget;
	private final RtlVectorSignal indexSignal;

	public RtlVectorTargetIndexSelection(RtlRealm realm, RtlVectorAssignmentTarget containerTarget, RtlVectorSignal indexSignal) {
		super(realm);
		if (containerTarget.getWidth() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("container of width " + containerTarget.getWidth() + " is too small for index of width " + indexSignal.getWidth());
		}
		this.containerTarget = checkSameRealm(containerTarget);
		this.indexSignal = checkSameRealm(indexSignal);
	}

	public RtlVectorAssignmentTarget getContainerTarget() {
		return containerTarget;
	}

	public RtlVectorSignal getIndexSignal() {
		return indexSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getNextValue() {
		return containerTarget.getNextValue().select(indexSignal.getValue().getAsUnsignedInt());
	}

	@Override
	public void setNextValue(boolean nextValue) {
		int index = indexSignal.getValue().getAsUnsignedInt();
		VectorValue nextContainerValue = containerTarget.getNextValue();
		VectorValue updatedValue;
		if (index < nextContainerValue.getWidth()) {
			if (index == 0) {
				VectorValue upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, 1);
				updatedValue = upper.concat(nextValue);
			} else if (index == nextContainerValue.getWidth() - 1) {
				VectorValue lower = nextContainerValue.select(index - 1, 0);
				updatedValue = lower.prepend(nextValue);
			} else {
				VectorValue upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, index + 1);
				VectorValue lower = nextContainerValue.select(index - 1, 0);
				updatedValue = upper.concat(nextValue).concat(lower);
			}
		} else {
			updatedValue = nextContainerValue;
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
		out.print(indexSignal);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		containerTarget.analyzeSignalUsage(consumer);
		consumer.consumeSignalUsage(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

}
