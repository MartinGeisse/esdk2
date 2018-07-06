/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorConstant extends RtlItem implements RtlVectorSignal {

	private final VectorValue value;

	public RtlVectorConstant(RtlDomain design, VectorValue value) {
		super(design);
		this.value = value;
	}

	public static RtlVectorConstant ofUnsigned(RtlDomain design, int width, int value) {
		return new RtlVectorConstant(design, VectorValue.ofUnsigned(width, value));
	}

	@Override
	public int getWidth() {
		return value.getWidth();
	}

	@Override
	public VectorValue getValue() {
		return value;
	}

	@Override
	public boolean compliesWith(VerilogGenerator.VerilogExpressionNesting nesting) {
		return true;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		value.printVerilogExpression(out);
	}

}
