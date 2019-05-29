package name.martingeisse.esdk.riscv.implementation;

import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;
import name.martingeisse.esdk.library.util.ClockStepper;
import name.martingeisse.esdk.riscv.rtl.Multicycle;

/**
 * Allows to single-step (instruction-wise) through a RISC-V design.
 * <p>
 * To make this class work correctly, no other code should drive the clock network or start or stop simulation.
 */
public class InstructionStepper extends RtlSimulationItem {

	private final ClockStepper clockStepper;
	private final Multicycle.Implementation cpu;

	public InstructionStepper(ClockStepper clockStepper, Multicycle.Implementation cpu) {
		super(clockStepper.getRealm());
		this.clockStepper = clockStepper;
		this.cpu = cpu;
	}

	public ClockStepper getClockStepper() {
		return clockStepper;
	}

	public Multicycle.Implementation getCpu() {
		return cpu;
	}

	/**
	 * This method can be used to skip the "preamble" before fetching the first instruction without actually stepping
	 * over the first instruction. Fetching is chosen as the starting point of the instruction because we can
	 * observe the PC on the memory address port.
	 */
	public void skipUntilFetching() {
		while (cpu._state.getValue().getBitsAsInt() != 1) {
			clockStepper.step();
		}
	}

	public void step() {
		skipUntilFetching();
		while (cpu._state.getValue().getBitsAsInt() == 1) {
			clockStepper.step();
		}
		clockStepper.step();
		skipUntilFetching();
	}

	public void step(int cycles) {
		for (int i = 0; i < cycles; i++) {
			step();
		}
	}

}
