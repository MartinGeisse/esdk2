/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.pin.simulation;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * This similar to an {@link RtlSimulatedSettableVectorSignal} except that won't complain about synthesis as long as it is not
 * asked for an implementation expression.
 */
public class VectorPinSimulationSignal extends RtlItem implements RtlVectorSignal {

	private final int width;
	private VectorValue value;

	public VectorPinSimulationSignal(RtlRealm realm, int width) {
		super(realm);
		this.width = width;
		this.value = VectorValue.of(width, 0);
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return value;
	}

	public void setValue(VectorValue value) {
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("get vector value of wrong width " + value.getWidth() + ", expected " + width);
		}
		this.value = value;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot print an implementation expression for " + this);
	}

}
