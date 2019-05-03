package name.martingeisse.esdk.core.rtl.signal;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class RtlSwitchSignal<B extends RtlSignal> extends RtlItem implements RtlSignal {

	private final RtlVectorSignal selector;
	private final List<Case<B>> cases;
	private B defaultSignal;

	public RtlSwitchSignal(RtlRealm realm, RtlVectorSignal selector) {
		super(realm);
		this.selector = selector;
		this.cases = new ArrayList<>();
		this.defaultSignal = null;
	}

	public final RtlVectorSignal getSelector() {
		return selector;
	}

	public final ImmutableList<Case<B>> getCases() {
		return ImmutableList.copyOf(cases);
	}

	public final B getDefaultSignal() {
		return defaultSignal;
	}

	public final void setDefaultSignal(B defaultSignal) {
		validateOnAdd(defaultSignal);
		this.defaultSignal = defaultSignal;
	}

	public final void addCase(VectorValue selectorValue, B branch) {
		addCase(ImmutableList.of(selectorValue), branch);
	}

	public final void addCase(VectorValue selectorValue1, VectorValue selectorValue2, B branch) {
		addCase(ImmutableList.of(selectorValue1, selectorValue2), branch);
	}

	public final void addCase(VectorValue selectorValue1, VectorValue selectorValue2,
						VectorValue selectorValue3, B branch) {
		addCase(ImmutableList.of(selectorValue1, selectorValue2, selectorValue3), branch);
	}

	public final void addCase(ImmutableList<VectorValue> selectorValues, B branch) {
		for (VectorValue selectorValue : selectorValues) {
			if (selectorValue.getWidth() != selector.getWidth()) {
				throw new IllegalArgumentException("selector value has width " + selectorValue.getWidth() +
					", expected " + selector.getWidth());
			}
		}
		validateOnAdd(branch);
		cases.add(new Case<>(getRealm(), selectorValues, branch));
	}

	protected abstract void validateOnAdd(B branch);

	public static final class Case<B extends RtlSignal> {

		private final ImmutableList<VectorValue> selectorValues;
		private final B branch;

		public Case(RtlRealm realm, ImmutableList<VectorValue> selectorValues, B branch) {
			this.selectorValues = selectorValues;
			this.branch = branch;
		}

		public ImmutableList<VectorValue> getSelectorValues() {
			return selectorValues;
		}

		public B getBranch() {
			return branch;
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
