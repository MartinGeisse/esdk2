package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlDomain;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.statement.RtlStatement;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;

/**
 * A signal whose value can be set by high-level models. This is meant as a bridge to simulate high-level models and
 * RTL models together.
 *
 * Unlike {@link RtlProceduralSignal}, this class does not work together with {@link RtlStatement}. This makes it
 * easier to use, but it cannot be synthesized. Also, changes to the value are reflected directly, so it is not
 * possible to update multiple settable signals synchronously to a clock edge.
 */
public abstract class RtlSettableSignal extends RtlItem implements RtlSignal {

	RtlSettableSignal(RtlDomain domain) {
		super(domain);
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot synthesize " + getClass());
	}

}
