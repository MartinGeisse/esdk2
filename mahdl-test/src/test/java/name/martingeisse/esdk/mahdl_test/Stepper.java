package name.martingeisse.esdk.mahdl_test;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 * Allows to single-step through an RTL design with a single clock.
 * <p>
 * To make this class work correctly, no other code should drive the same clock network or start or stop simulation.
 */
public class Stepper extends RtlItem {

	private final RtlClockNetwork clock;
	private final int clockPeriod;

	public Stepper(RtlRealm realm, int clockPeriod) {
		this(new RtlClockNetwork(realm), clockPeriod);
	}

	public Stepper(RtlClockNetwork clock, int clockPeriod) {
		super(clock.getRealm());
		this.clock = clock;
		this.clockPeriod = clockPeriod;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public int getClockPeriod() {
		return clockPeriod;
	}

	public void step() {
		step(1);
	}

	public void step(int cycles) {
		stepInternal(cycles);
		getDesign().continueSimulation();
	}

	private void stepInternal(int cycles) {
		if (cycles < 1) {
			getDesign().stopSimulation();
		} else {
			fire(() -> {
				clock.simulateClockEdge();
				stepInternal(cycles - 1);
			}, clockPeriod);
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
