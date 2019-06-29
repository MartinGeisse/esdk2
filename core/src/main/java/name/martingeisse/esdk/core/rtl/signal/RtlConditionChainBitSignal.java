package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an if/elseif/elseif/.../else chain for a signal. This is easier to use than building such a chain manually.
 */
public final class RtlConditionChainBitSignal extends RtlItem implements RtlBitSignal {

	private final List<Case> cases = new ArrayList<>();
	private RtlBitSignal defaultCase;
	private RtlBitSignal materializedChain;

	public RtlConditionChainBitSignal(RtlRealm realm) {
		super(realm);
	}

	public void when(RtlBitSignal condition, RtlBitSignal signal) {
		cases.add(new Case(condition, signal));
	}

	public void when(RtlBitSignal condition, boolean value) {
		when(condition, new RtlBitConstant(getRealm(), value));
	}

	public void otherwise(RtlBitSignal signal) {
		this.defaultCase = signal;
	}

	public void otherwise(boolean value) {
		otherwise(new RtlBitConstant(getRealm(), value));
	}

	private static final class Case {

		private final RtlBitSignal condition;
		private final RtlBitSignal signal;

		public Case(RtlBitSignal condition, RtlBitSignal signal) {
			this.condition = condition;
			this.signal = signal;
		}

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
	public boolean getValue() {
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
				materializedChain = new RtlConditionalBitOperation(getRealm(), aCase.condition, aCase.signal, materializedChain);
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
