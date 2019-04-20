package name.martingeisse.esdk.core.rtl.signal;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatement;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlSwitchSignal extends RtlStatement {

	private final RtlVectorSignal selector;
	private final List<Case> cases;
	private RtlVectorSignal defaultSignal;

	public RtlSwitchSignal(RtlRealm realm, RtlVectorSignal selector) {
		super(realm);
		this.selector = selector;
		this.cases = new ArrayList<>();
		this.defaultSignal = null;
	}

	public RtlVectorSignal getSelector() {
		return selector;
	}

	public ImmutableList<Case> getCases() {
		return ImmutableList.copyOf(cases);
	}

	public RtlVectorSignal getDefaultSignal() {
		return defaultSignal;
	}

	public void addCase(VectorValue selectorValue, RtlVectorSignal branch) {
		addCase(ImmutableList.of(selectorValue), branch);
	}

	public void addCase(VectorValue selectorValue1, VectorValue selectorValue2, RtlVectorSignal branch) {
		addCase(ImmutableList.of(selectorValue1, selectorValue2), branch);
	}

	public void addCase(VectorValue selectorValue1, VectorValue selectorValue2,
						VectorValue selectorValue3, RtlVectorSignal branch) {
		addCase(ImmutableList.of(selectorValue1, selectorValue2, selectorValue3), branch);
	}

	public void addCase(ImmutableList<VectorValue> selectorValues, RtlVectorSignal branch) {
		for (VectorValue selectorValue : selectorValues) {
			if (selectorValue.getWidth() != selector.getWidth()) {
				throw new IllegalArgumentException("selector value has width " + selectorValue.getWidth() +
					", expected " + selector.getWidth());
			}
		}
		cases.add(new Case(getRealm(), selectorValues, branch));
	}

	public static final class Case {

		private final ImmutableList<VectorValue> selectorValues;
		private final RtlVectorSignal branch;

		public Case(RtlRealm realm, ImmutableList<VectorValue> selectorValues, RtlVectorSignal branch) {
			this.selectorValues = selectorValues;
			this.branch = branch;
		}

		public ImmutableList<VectorValue> getSelectorValues() {
			return selectorValues;
		}

		public RtlVectorSignal getBranch() {
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
