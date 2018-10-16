package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an if/elseif/elseif/.../else chain. This is easier to use than building such a chain manually.
 */
public final class RtlConditionChainStatement extends RtlStatement {

	private final List<Case> cases = new ArrayList<>();
	private RtlStatement defaultCase;
	private RtlStatement materializedChain;

	public RtlConditionChainStatement(RtlRealm realm) {
		super(realm);
	}

	public void when(RtlBitSignal condition, RtlStatement statement) {
		cases.add(new Case(condition, statement));
	}

	public RtlStatementSequence when(RtlBitSignal condition) {
		RtlStatementSequence branch = new RtlStatementSequence(getRealm());
		when(condition, branch);
		return branch;
	}

	public void otherwise(RtlStatement defaultCase) {
		this.defaultCase = defaultCase;
	}

	public RtlStatementSequence otherwise() {
		if (defaultCase instanceof RtlStatementSequence) {
			return (RtlStatementSequence) defaultCase;
		}
		RtlStatementSequence branch = new RtlStatementSequence(getRealm());
		if (defaultCase != null) {
			branch.addStatement(defaultCase);
		}
		defaultCase = branch;
		return branch;
	}

	private static final class Case {

		private final RtlBitSignal condition;
		private final RtlStatement statement;

		public Case(RtlBitSignal condition, RtlStatement statement) {
			this.condition = condition;
			this.statement = statement;
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
		for (Case aCase : cases) {
			if (aCase.condition.getValue()) {
				aCase.statement.execute();
				return;
			}
		}
		if (defaultCase != null) {
			defaultCase.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	private void materialize() {
		if (materializedChain == null) {
			RtlStatement result = defaultCase;
			for (int i = cases.size() - 1; i >= 0; i--) {
				Case aCase = cases.get(i);
				RtlWhenStatement when = new RtlWhenStatement(getRealm(), aCase.condition);
				when.getThenBranch().addStatement(aCase.statement);
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

}
