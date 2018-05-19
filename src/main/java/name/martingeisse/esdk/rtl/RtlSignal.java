/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import java.util.function.Consumer;

/**
 * Note: If an implementation redefines equals() / hashCode(), then "equal" signals must be exchangeable without
 * changes to the RTL semantics. The Verilog code generator will use these methods, not object identity, to detect
 * re-use of signal objects.
 */
public interface RtlSignal extends RtlItemOwned {

	/**
	 * Returns true iff the expression for this signal can be generated with the specified nesting.
	 *
	 * The default implementation only complies with ALL nesting. This is correct in all cases but may extract an
	 * expression to a helper signal unnecessarily.
	 */
	default boolean compliesWith(VerilogDesignGenerator.VerilogExpressionNesting nesting) {
		return nesting == VerilogDesignGenerator.VerilogExpressionNesting.ALL;
	}

	/**
	 * Writes a Verilog expression for this signal. See {@link VerilogExpressionWriter} for details.
	 */
	void printVerilogExpression(VerilogExpressionWriter out);

}
