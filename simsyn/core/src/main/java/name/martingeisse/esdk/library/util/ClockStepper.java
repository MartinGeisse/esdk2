package name.martingeisse.esdk.library.util;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;

/**
 * Allows to single-step (clock cycle-wise) through an RTL design with a single clock.
 * <p>
 * To make this class work correctly, no other code should drive the same clock network or start or stop simulation.
 */
public class ClockStepper extends RtlSimulationItem {

	private final RtlClockNetwork clock;
	private final int clockPeriod;

	public ClockStepper(RtlRealm realm, int clockPeriod) {
		this(new RtlClockNetwork(realm), clockPeriod);
	}

	public ClockStepper(RtlClockNetwork clock, int clockPeriod) {
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

}
