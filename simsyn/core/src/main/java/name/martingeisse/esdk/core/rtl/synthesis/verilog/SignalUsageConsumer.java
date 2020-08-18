/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public interface SignalUsageConsumer {

	// note: signal may be null for convenience -- in that case, this method has no effect
	void consumeSignalUsage(RtlSignal signal, VerilogExpressionNesting nesting);

	VerilogExpressionWriter getFakeExpressionWriter();

}
