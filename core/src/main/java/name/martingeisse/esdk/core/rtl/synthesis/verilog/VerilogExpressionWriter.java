/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 * Helper class to turn signals into expressions. Signal classes should expect to be called twice:
 * - the first call is a "dry-run" to collect all relevant signals, detect duplicates and determine allowed expression
 *   nesting (generating helper wires to resolve forbidden nesting, [1])
 * - the second call actually writes the expression.
 *
 * Signal classes should use {@link #printSignal(RtlSignal, VerilogExpressionNesting)} to print
 * sub-expressions, or equivalently,
 * {@link RtlSignal#printVerilogExpression(VerilogExpressionWriter, VerilogExpressionNesting)}.
 * Directly calling {@link RtlSignal#printVerilogImplementationExpression(VerilogExpressionWriter)} may produce wrong
 * results and is not allowed.
 */
public interface VerilogExpressionWriter {
	VerilogExpressionWriter print(String s);
	VerilogExpressionWriter print(int i);
	VerilogExpressionWriter print(char c);
	VerilogExpressionWriter printSignal(RtlSignal signal, VerilogExpressionNesting nesting);
	VerilogExpressionWriter printMemory(RtlProceduralMemory memory);
}
