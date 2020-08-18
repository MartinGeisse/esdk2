package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * A register whose input can be set via a setter method during simulation. This is useful to implement the RTL
 * interface of a simulated component.
 *
 * Unlike {@link RtlSimulatedSettableSignal}, this class is used to update a signal synchronously to a clock edge.
 *
 * Usage: call setNextValue() during computeNextState().
 *
 * Using this signal in a way that is not relevant to synthesis, such as a simulation replacement signal of instance
 * ports, is allowed.
 */
public abstract class RtlSimulatedRegister extends RtlClockedItem implements RtlSignal {

	RtlSimulatedRegister(RtlClockNetwork clock) {
		super(clock);
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
