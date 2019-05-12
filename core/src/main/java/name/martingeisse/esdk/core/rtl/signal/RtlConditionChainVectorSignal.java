package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an if/elseif/elseif/.../else chain for a signal. This is easier to use than building such a chain manually.
 */
public final class RtlConditionChainVectorSignal extends RtlItem implements RtlVectorSignal {

	private final int width;
	private final List<Case> cases = new ArrayList<>();
	private RtlVectorSignal defaultCase;
	private RtlVectorSignal materializedChain;

	public RtlConditionChainVectorSignal(RtlRealm realm, int width) {
		super(realm);
		this.width = width;
	}

	private void checkWidth(RtlVectorSignal signal) {
		if (signal.getWidth() != width) {
			throw new IllegalArgumentException("signal width " + signal.getWidth() + " should be " + width);
		}
	}

	public void when(RtlBitSignal condition, RtlVectorSignal signal) {
		checkWidth(signal);
		cases.add(new Case(condition, signal));
	}

	public void otherwise(RtlVectorSignal signal) {
		checkWidth(signal);
		this.defaultCase = signal;
	}

	private static final class Case {

		private final RtlBitSignal condition;
		private final RtlVectorSignal signal;

		public Case(RtlBitSignal condition, RtlVectorSignal signal) {
			this.condition = condition;
			this.signal = signal;
		}

	}

	@Override
	public int getWidth() {
		return width;
	}

	private void checkDefaultCaseExists() {
		if (defaultCase == null) {
			throw new IllegalStateException("no default case for condition chain signal");
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		checkDefaultCaseExists();
	}

	@Override
	public VectorValue getValue() {
		for (Case aCase : cases) {
			if (aCase.condition.getValue()) {
				return aCase.signal.getValue();
			}
		}
		return defaultCase.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	protected void materialize() {
		if (materializedChain == null) {
			checkDefaultCaseExists();
			materializedChain = defaultCase;
			for (int i = cases.size() - 1; i >= 0; i--) {
				Case aCase = cases.get(i);
				materializedChain = new RtlConditionalVectorOperation(getRealm(), aCase.condition, aCase.signal, materializedChain);
				materializedChain.getRtlItem().setName(getName());
			}
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return materializedChain.getRtlItem().getVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		materializedChain.printVerilogImplementationExpression(out);
	}

}
