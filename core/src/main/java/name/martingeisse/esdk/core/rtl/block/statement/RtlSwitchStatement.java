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
		VectorValue actualSelectorValue = selector.getValue();
		for (Case aCase : cases) {
			for (VectorValue caseSelectorValue : aCase.getSelectorValues()) {
				if (actualSelectorValue.equals(caseSelectorValue)) {
					aCase.getBranch().execute();
					return;
				}
			}
		}
		if (defaultBranch != null) {
			defaultBranch.execute();
		}
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		for (Case aCase : cases) {
			aCase.getBranch().analyzeSignalUsage(consumer);
		}
		if (defaultBranch != null) {
			defaultBranch.analyzeSignalUsage(consumer);
		}
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		out.print("case (");
		out.print(selector);
		out.println(")");
		out.println();
		out.startIndentation();
		for (Case aCase : cases) {
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
			aCase.getBranch().printVerilogStatements(out);
			out.endIndentation();
			out.println("end");
			out.println();
		}
		if (defaultBranch != null) {
			out.indent();
			out.println("default: begin");
			out.startIndentation();
			defaultBranch.printVerilogStatements(out);
			out.endIndentation();
			out.println("end");
			out.println();
		}
		out.endIndentation();
		out.indent();
		out.println("endcase");
	}

}
