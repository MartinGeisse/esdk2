package name.martingeisse.esdk.riscv.instruction.extended;

import name.martingeisse.esdk.riscv.instruction.InstructionLevelRiscv;

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
