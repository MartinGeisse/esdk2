package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog_v2.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlBitRepetition extends RtlItem implements RtlVectorSignal {

	private final RtlBitSignal bitSignal;
	private final int repetitions;

	public RtlBitRepetition(RtlRealm realm, RtlBitSignal bitSignal, int repetitions) {
		super(realm);
		this.bitSignal = bitSignal;
		this.repetitions = repetitions;
	}

	public RtlBitSignal getBitSignal() {
		return bitSignal;
	}

	public int getRepetitions() {
		return repetitions;
	}

	@Override
	public int getWidth() {
		return repetitions;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return VectorValue.ofUnsigned(repetitions, bitSignal.getValue() ? ((1 << repetitions) - 1) : 0);
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print('{');
		out.print(repetitions);
		out.print('{');
		out.print(bitSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print("}}");
	}

}
