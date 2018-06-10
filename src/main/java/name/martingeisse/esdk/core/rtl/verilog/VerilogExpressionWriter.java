/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.verilog;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;

/**
 * Helper class to turn signals into expressions. Signal classes should expect to be called twice:
 * - the first call is a "dry-run" to collect all relevant signals, detect duplicates and determine allowed expression
 *   nesting (generating helper wires to resolve forbidden nesting, [1])
 * - the second call actually writes the expression.
 *
 * Signal classes should use {@link #print(RtlSignal, VerilogDesignGenerator.VerilogExpressionNesting)} to print
 * sub-expressions. Directly calling {@link RtlSignal#printVerilogExpression(VerilogExpressionWriter)} will prevent the
 * sub-expression from being extracted for shared signals or invalid nesting. In some cases, this can even generate
 * Verilog with wrong semantics.
 */
public interface VerilogExpressionWriter {

	VerilogExpressionWriter print(String s);
	VerilogExpressionWriter print(int i);
	VerilogExpressionWriter print(char c);

	VerilogExpressionWriter print(RtlSignal signal, VerilogDesignGenerator.VerilogExpressionNesting nesting);

	VerilogExpressionWriter printProceduralSignalName(RtlProceduralSignal signal);

}
