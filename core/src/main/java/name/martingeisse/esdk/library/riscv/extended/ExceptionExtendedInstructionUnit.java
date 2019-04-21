package name.martingeisse.esdk.library.riscv.extended;

import name.martingeisse.esdk.library.riscv.InstructionLevelRiscv;
import name.martingeisse.esdk.library.riscv.floating.FloatingPointUnit;

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
