/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog_v2;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;

/**
 *
 */
public interface SignalUsageConsumer {

	void consumeSignalUsage(RtlSignal signal, VerilogExpressionNesting nesting);

	VerilogExpressionWriter getFakeExpressionWriter();

}
