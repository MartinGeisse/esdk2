/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.verilog.VerilogDesignGenerator;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 *
 */
public final class RtlVectorConstant extends RtlItem implements RtlVectorSignal {

	private final RtlVectorValue value;

	public RtlVectorConstant(RtlDesign design, RtlVectorValue value) {
		super(design);
		this.value = value;
	}

	public static RtlVectorConstant from(RtlDesign design, int width, int value) {
		return new RtlVectorConstant(design, RtlVectorValue.from(width, value));
	}

	@Override
	public int getWidth() {
		return value.getWidth();
	}

	@Override
	public RtlVectorValue getValue() {
		return value;
	}

	@Override
	public boolean compliesWith(VerilogDesignGenerator.VerilogExpressionNesting nesting) {
		return true;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		value.printVerilogExpression(out);
	}

}
