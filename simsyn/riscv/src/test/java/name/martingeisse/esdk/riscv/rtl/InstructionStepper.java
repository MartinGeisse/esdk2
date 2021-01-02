package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.util.ClockStepper;

/**
 * Allows to single-step (instruction-wise) through a RISC-V design.
 * <p>
 * To make this class work correctly, no other code should drive the clock network or start or stop simulation.
 */
public class InstructionStepper extends RtlSimulationItem {

	private final ClockStepper clockStepper;
	private final Multicycle.Implementation cpu;
	private final RtlSimulatedSettableVectorSignal instruction;
	private final RtlSimulatedSettableBitSignal instructionAcknowledge;

	public InstructionStepper(ClockStepper clockStepper, Multicycle.Implementation cpu) {
		super(clockStepper.getRealm());
		RtlRealm realm = clockStepper.getRealm();
		this.clockStepper = clockStepper;
		this.cpu = cpu;
		this.instruction = new RtlSimulatedSettableVectorSignal(realm, 32);
		this.instructionAcknowledge = new RtlSimulatedSettableBitSignal(realm);
		cpu.setMemoryReadData(instruction);
		cpu.setMemoryAcknowledge(instructionAcknowledge.or(cpu.getMemoryWrite()));
	}

	public ClockStepper getClockStepper() {
		return clockStepper;
	}

	public Multicycle.Implementation getCpu() {
		return cpu;
	}

	private boolean isFetching() {
		return (cpu._state.getValue().equals(Multicycle.Implementation._STATE_FETCH) ||
				cpu._state.getValue().equals(Multicycle.Implementation._STATE_FINISH_EARLY_FETCH));
	}

	/**
	 * This method can be used to skip the "preamble" before fetching the first instruction without actually stepping
	 * over the first instruction. Fetching is chosen as the starting point of the instruction because we can
	 * observe the PC on the memory address port.
	 */
	public void skipUntilFetching() {
		while (!isFetching()) {
			clockStepper.step();
		}
	}

	public void step(int instruction) {
		skipUntilFetching();
		this.instruction.setValue(VectorValue.of(32, instruction & 0xffff_ffffL));
		this.instructionAcknowledge.setValue(true);
		while (isFetching()) {
			clockStepper.step();
		}
		this.instructionAcknowledge.setValue(false);
		clockStepper.step(); // TODO unnecessary!?
		skipUntilFetching();
	}

}
