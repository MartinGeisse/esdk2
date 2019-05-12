package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralRegister;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatement;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

/**
 * A signal whose value can be set by high-level models. This is meant as a bridge to simulate high-level models and
 * RTL models together.
 *
 * Unlike {@link RtlProceduralRegister}, this class does not work together with {@link RtlStatement}. This makes it
 * easier to use, but it cannot be synthesized. Also, changes to the value are reflected directly, so it is not
 * possible to update multiple settable signals synchronously to a clock edge.
 *
 * Using this signal in a way that is not relevant to synthesis, such as a simulation replacement signal of instance
 * ports, is allowed.
 */
public abstract class RtlSimulatedSettableSignal extends RtlItem implements RtlSignal {

	RtlSimulatedSettableSignal(RtlRealm realm) {
		super(realm);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public final VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot print an implementation expression for " + this);
	}

}
