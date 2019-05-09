/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorConstant extends RtlItem implements RtlVectorSignal {

	private final VectorValue value;

	public RtlVectorConstant(RtlRealm realm, VectorValue value) {
		super(realm);
		this.value = value;
	}

	public static RtlVectorConstant of(RtlRealm realm, int width, long value) {
		return new RtlVectorConstant(realm, VectorValue.of(width, value));
	}

	@Override
	public int getWidth() {
		return value.getWidth();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return value;
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
		return true;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		value.printVerilogExpression(out);
	}

}
