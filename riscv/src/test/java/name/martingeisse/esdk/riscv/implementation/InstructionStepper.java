package name.martingeisse.esdk.riscv.implementation;

import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;
import name.martingeisse.esdk.library.util.ClockStepper;

/**
 * Allows to single-step (instruction-wise) through a RISC-V design.
 * <p>
 * To make this class work correctly, no other code should drive the clock network or start or stop simulation.
 */
public class InstructionStepper extends RtlSimulationItem {

	private final ClockStepper clockStepper;
	private final Multicycle cpu;

	public InstructionStepper(ClockStepper clockStepper, Multicycle cpu) {
		super(clockStepper.getRealm());
		this.clockStepper = clockStepper;
		this.cpu = cpu;
	}

	public ClockStepper getClockStepper() {
		return clockStepper;
	}

	public Multicycle getCpu() {
		return cpu;
	}

	/**
	 * This method can be used to skip the "preamble" before the first instruction without actually stepping over the
	 * first instruction.
	 */
	public void skipUntilFetching() {
		while (!cpu.getInstructionReadEnable().getValue()) {
			clockStepper.step();
		}
	}

	public void step() {
		skipUntilFetching();
		while (cpu.getInstructionReadEnable().getValue() && !cpu.getInstructionReadAcknowledge().getValue()) {
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
