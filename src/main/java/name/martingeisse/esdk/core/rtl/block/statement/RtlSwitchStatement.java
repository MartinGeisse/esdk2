package name.martingeisse.esdk.core.rtl.block.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlSwitchStatement extends RtlStatement {

	private final RtlVectorSignal selector;
	private final List<Case> cases;
	private final RtlStatementSequence defaultBranch;

	public RtlSwitchStatement(RtlRealm realm, RtlVectorSignal selector) {
		super(realm);
		this.selector = selector;
		this.cases = new ArrayList<>();
		this.defaultBranch = new RtlStatementSequence(realm);
	}

	public RtlVectorSignal getSelector() {
		return selector;
	}

	public ImmutableList<Case> getCases() {
		return ImmutableList.copyOf(cases);
	}

	public RtlStatementSequence getDefaultBranch() {
		return defaultBranch;
	}

	public RtlStatementSequence addCase(VectorValue... selectorValues) {
		return addCase(ImmutableList.copyOf(selectorValues));
	}

	public RtlStatementSequence addCase(ImmutableList<VectorValue> selectorValues) {
		for (VectorValue selectorValue : selectorValues) {
			if (selectorValue.getWidth() != selector.getWidth()) {
				throw new IllegalArgumentException("selector value has width " + selectorValue.getWidth() +
					", expected " + selector.getWidth());
			}
		}
		Case aCase = new Case(getRealm(), selectorValues);
		cases.add(aCase);
		return aCase.getBranch();
	}

	public static final class Case {

		private final ImmutableList<VectorValue> selectorValues;
		private final RtlStatementSequence branch;

		public Case(RtlRealm realm, ImmutableList<VectorValue> selectorValues) {
			this.selectorValues = selectorValues;
			this.branch = new RtlStatementSequence(realm);
		}

		public ImmutableList<VectorValue> getSelectorValues() {
			return selectorValues;
		}

		public RtlStatementSequence getBranch() {
			return branch;
		}
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
