package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlOneBitVectorSignal extends RtlItem implements RtlVectorSignal {

	private final RtlBitSignal bitSignal;

	public RtlOneBitVectorSignal(RtlRealm realm, RtlBitSignal bitSignal) {
		super(realm);
		this.bitSignal = bitSignal;
	}

	public RtlBitSignal getBitSignal() {
		return bitSignal;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return VectorValue.ofUnsigned(1, bitSignal.getValue() ? 1 : 0);
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return bitSignal.compliesWith(nesting);
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print(bitSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

}
