package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;
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
		VectorValue result = VectorValue.of(0, 0);
		for (int i = 0; i < repetitions; i++) {
			result = result.concat(single);
		}
		return result;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print('{');
		out.print(repetitions);
		out.print('{');
		out.printSignal(vectorSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print("}}");
	}

}
