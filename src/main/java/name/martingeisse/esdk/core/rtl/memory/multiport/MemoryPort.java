package name.martingeisse.esdk.core.rtl.memory.multiport;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public interface MemoryPort {

	void validate();

	void prepareSynthesis(SynthesisPreparationContext context);

	void analyzeSignalUsage(SignalUsageConsumer consumer);

	void printDeclarations(VerilogWriter out);

	void printImplementation(VerilogWriter out);

}
