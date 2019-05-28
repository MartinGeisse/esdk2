package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * This is a simplified wrapper around a memory with a single asynchronous read port.
 */
public final class RtlLookupTable extends RtlItem implements RtlVectorSignal {

	private final RtlMemory memory;
	private final RtlAsynchronousMemoryReadPort port;

	public RtlLookupTable(RtlRealm realm, Matrix matrix, RtlVectorSignal indexSignal) {
		this(realm, new RtlMemory(realm, matrix), indexSignal);
	}

	public RtlLookupTable(RtlRealm realm, int width, RtlVectorSignal indexSignal) {
		this(realm, new RtlMemory(realm, 1 << indexSignal.getWidth(), width), indexSignal);
	}

	private RtlLookupTable(RtlRealm realm, RtlMemory memory, RtlVectorSignal indexSignal) {
		super(realm);
		this.memory = memory;
		this.port = memory.createAsynchronousReadPort();
		this.port.setAddressSignal(indexSignal);
	}

	public RtlMemory getMemory() {
		return memory;
	}

	public Matrix getMatrix() {
		return memory.getMatrix();
	}

	@Override
	public int getWidth() {
		return port.getReadDataSignal().getWidth();
	}

	@Override
	public VectorValue getValue() {
		return port.getReadDataSignal().getValue();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		port.getReadDataSignal().printVerilogImplementationExpression(out);
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
