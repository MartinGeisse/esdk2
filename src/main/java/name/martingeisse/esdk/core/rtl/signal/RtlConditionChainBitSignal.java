package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an if/elseif/elseif/.../else chain for a signal. This is easier to use than building such a chain manually.
 */
public final class RtlConditionChainBitSignal extends RtlItem implements RtlBitSignal {

	private final List<Case> cases = new ArrayList<>();
	private RtlBitSignal defaultCase;

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

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		if (defaultCase == null) {
			throw new IllegalStateException("no default case for condition chain signal");
		}
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

	// TODO materialize cannot work because the generated contributions won't be respected by the verilog generator
	/*
	private void materialize() {
		if (materializedChain == null) {
			RtlStatement result = defaultCase;
			for (int i = cases.size() - 1; i >= 0; i--) {
				Case aCase = cases.get(i);
				RtlWhenStatement when = new RtlWhenStatement(getRealm(), aCase.condition);
				when.getThenBranch().addStatement(aCase.signal);
				if (result != null) {
					when.getOtherwiseBranch().addStatement(result);
				}
				result = when;
			}
			materializedChain = (result == null ? new RtlNopStatement(getRealm()) : result);
		}
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		materialize();
		materializedChain.analyzeSignalUsage(consumer);
	}


	@Override
	public void printVerilogStatements(VerilogWriter out) {
		materialize();
		materializedChain.printVerilogStatements(out);
	}
*/

	@Override
	public VerilogContribution getVerilogContribution() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException();
	}

}
