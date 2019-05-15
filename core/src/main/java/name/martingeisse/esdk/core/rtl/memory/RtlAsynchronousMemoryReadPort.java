package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.model.validation.ValidationContext;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Asynchronous read ports are special because they do not need a clock at all.
 */
public final class RtlAsynchronousMemoryReadPort extends RtlItem implements RtlMemoryPort {

	private final RtlMemory memory;
	private final RtlVectorSignal readDataSignal;
	private RtlVectorSignal addressSignal;

	RtlAsynchronousMemoryReadPort(RtlMemory memory) {
		super(memory.getRealm());
		this.memory = memory;
		this.readDataSignal = new ReadDataSignal(getRealm());
	}

	public RtlMemory getMemory() {
		return memory;
	}

	public RtlVectorSignal getReadDataSignal() {
		return readDataSignal;
	}

	public RtlVectorSignal getAddressSignal() {
		return addressSignal;
	}

	public void setAddressSignal(RtlVectorSignal addressSignal) {
		if (addressSignal.getWidth() > 30) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() + " not supported");
		}
		if (1 << addressSignal.getWidth() > memory.getMatrix().getRowCount()) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() +
				" is too large for matrix row count " + memory.getMatrix().getRowCount());
		}
		this.addressSignal = addressSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void validate(ValidationContext context) {
		if (addressSignal == null) {
			context.reportError("no address signal");
		}
	}

	@Override
	public void prepareSynthesis(SynthesisPreparationContext context) {
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(addressSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

	@Override
	public void printDeclarations(VerilogWriter out) {
	}

	@Override
	public void printImplementation(VerilogWriter out) {
	}

	final class ReadDataSignal extends RtlItem implements RtlVectorSignal {

		ReadDataSignal(RtlRealm realm) {
			super(realm);
		}

		@Override
		public int getWidth() {
			return memory.getMatrix().getColumnCount();
		}

		@Override
		public VectorValue getValue() {
			return memory.getMatrix().getRow(addressSignal.getValue().getAsUnsignedInt());
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		@Override
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			out.print(memory.getMemorySignal(), VerilogExpressionNesting.ALL);
			out.print('[');
			out.print(addressSignal, VerilogExpressionNesting.ALL);
			out.print(']');
		}

	}

}
