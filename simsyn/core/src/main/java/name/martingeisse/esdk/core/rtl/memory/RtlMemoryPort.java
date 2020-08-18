package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlItemOwned;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public interface RtlMemoryPort extends RtlItemOwned {

	void prepareSynthesis(SynthesisPreparationContext context);

	void analyzeSignalUsage(SignalUsageConsumer consumer);

	void printDeclarations(VerilogWriter out);

	void printImplementation(VerilogWriter out);

}
