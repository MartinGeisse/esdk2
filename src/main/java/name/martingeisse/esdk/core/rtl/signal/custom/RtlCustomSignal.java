/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.custom;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 * Base class for {@link RtlCustomBitSignal} and {@link RtlCustomVectorSignal}.
 */
public abstract class RtlCustomSignal extends RtlItem implements RtlSignal {

	public RtlCustomSignal(RtlRealm realm) {
		super(realm);
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("This class does not support Verilog generation");
	}

}
