package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorRepetition extends RtlItem implements RtlVectorSignal {

	private final RtlVectorSignal vectorSignal;
	private final int repetitions;

	public RtlVectorRepetition(RtlRealm realm, RtlVectorSignal vectorSignal, int repetitions) {
		super(realm);
		this.vectorSignal = vectorSignal;
		this.repetitions = repetitions;
	}

	public RtlVectorSignal getVectorSignal() {
		return vectorSignal;
	}

	public int getRepetitions() {
		return repetitions;
	}

	@Override
	public int getWidth() {
		return vectorSignal.getWidth() * repetitions;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		VectorValue single = vectorSignal.getValue();
		VectorValue result = VectorValue.ofUnsigned(0, 0);
		for (int i = 0; i < repetitions; i++) {
			result = result.concat(single);
		}
		return result;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print('{');
		out.print(repetitions);
		out.print('{');
		out.print(vectorSignal, VerilogGenerator.VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print("}}");
	}

}
