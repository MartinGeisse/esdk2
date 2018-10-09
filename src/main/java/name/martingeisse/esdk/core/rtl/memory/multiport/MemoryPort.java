package name.martingeisse.esdk.core.rtl.memory.multiport;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;

/**
 *
 */
public interface MemoryPort {

	void validate();

	void analyzeSignalUsage(SignalUsageConsumer consumer);

}
