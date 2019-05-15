package name.martingeisse.esdk.core.rtl.signal;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.*;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
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

	protected final B getCurrentlySelectedBranch() {
		VectorValue actualSelectorValue = selector.getValue();
		for (Case<B> aCase : cases) {
			for (VectorValue caseSelectorValue : aCase.getSelectorValues()) {
				if (actualSelectorValue.equals(caseSelectorValue)) {
					return aCase.getBranch();
				}
			}
		}
		if (defaultSignal == null) {
			throw new IllegalStateException("selector value " + actualSelectorValue +
				" did not match any case but there is no default branch");
		}
		return defaultSignal;
	}

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
		return new VerilogContribution() {

			private String name;

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				name = context.declareSignal(RtlSwitchSignal.this, "switch", VerilogSignalKind.REG, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.indent();
				out.println("always @(*) begin");
				out.startIndentation();
				out.indent();
				out.print("case (");
				out.print(selector);
				out.println(")");
				out.println();
				out.startIndentation();
				for (Case<B> aCase : cases) {
					out.indent();
					boolean firstSelectorValue = true;
					for (VectorValue selectorValue : aCase.selectorValues) {
						if (firstSelectorValue) {
							firstSelectorValue = false;
						} else {
							out.print(", ");
						}
						out.print(selectorValue);
					}
					out.println(": begin");
					out.startIndentation();
					out.print(RtlSwitchSignal.this);
					out.print(" <= ");
					out.print(aCase.branch);
					out.println(";");
					out.endIndentation();
					out.println("end");
					out.println();
				}
				if (defaultSignal != null) {
					out.indent();
					out.println("default: begin");
					out.startIndentation();
					out.print(RtlSwitchSignal.this);
					out.print(" <= ");
					out.print(defaultSignal);
					out.println(";");
					out.endIndentation();
					out.println("end");
					out.println();
				}
				out.endIndentation();
				out.indent();
				out.println("endcase");
				out.endIndentation();
				out.indent();
				out.println("end");
			}

		};
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return false;
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(selector, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		for (Case aCase : cases) {
			consumer.consumeSignalUsage(aCase.branch, VerilogExpressionNesting.ALL);
		}
		if (defaultSignal != null) {
			consumer.consumeSignalUsage(defaultSignal, VerilogExpressionNesting.ALL);
		}
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot generate implementation expression for RtlSwitchSignal");
	}

}
