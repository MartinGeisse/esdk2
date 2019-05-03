package name.martingeisse.esdk.riscv.simulator.extended;

import name.martingeisse.esdk.riscv.simulator.InstructionLevelRiscv;
import name.martingeisse.esdk.riscv.simulator.floating.FloatingPointUnit;

/**
 *
 */
public final class ExceptionExtendedInstructionUnit implements ExtendedInstructionUnit {

	private final InstructionLevelRiscv cpu;

	public ExceptionExtendedInstructionUnit(InstructionLevelRiscv cpu) {
		this.cpu = cpu;
	}

	@Override
	public void handleExtendedInstruction(int instruction) {
		cpu.triggerException(InstructionLevelRiscv.ExceptionType.ILLEGAL_INSTRUCTION);
	}

}
