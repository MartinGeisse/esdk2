/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogSignalKind;

/**
 * Note: If an implementation redefines equals() / hashCode(), then "equal" signals must be exchangeable without
 * changes to the RTL semantics. The Verilog code generator will use these methods, not object identity, to detect
 * re-use of signal objects.
 */
public interface RtlSignal extends RtlItemOwned {

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	default VerilogSignalKind getVerilogSignalKind() {
		return VerilogSignalKind.WIRE;
	}

	/**
	 * Returns true iff the expression for this signal can be generated with the specified nesting.
	 *
	 * The default implementation only complies with ALL nesting. This is correct in all cases but may extract an
	 * expression to a helper signal unnecessarily.
	 */
	default boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting == VerilogExpressionNesting.ALL;
	}

	/**
	 * Writes a Verilog expression for this signal. This method only exists so nobody calls
	 * {@link #printVerilogImplementationExpression(VerilogExpressionWriter)} accidentally. It is equivalent to
	 * {@link VerilogExpressionWriter#print(RtlSignal, VerilogExpressionNesting)}.
	 */
	default void printVerilogExpression(VerilogExpressionWriter out, VerilogExpressionNesting nesting) {
		out.print(this, nesting);
	}

	/**
	 * Writes a Verilog implementation expression for this signal. This method should only be called by the verilog
	 * generation core since it repeats the implementation expression for each usage of a shared signal. Also, not all
	 * signals support this method -- some need an extracted signal definition which is given a meaning by different
	 * means than an implementation expression (for example, switch-expressions and instance output ports).
	 */
	void printVerilogImplementationExpression(VerilogExpressionWriter out);

}
