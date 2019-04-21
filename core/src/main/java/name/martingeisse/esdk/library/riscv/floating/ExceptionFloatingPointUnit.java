package name.martingeisse.esdk.library.riscv.floating;

import name.martingeisse.esdk.library.riscv.InstructionLevelRiscv;

/**
 *
 */
public final class ExceptionFloatingPointUnit implements FloatingPointUnit {

	private final InstructionLevelRiscv cpu;

	public ExceptionFloatingPointUnit(InstructionLevelRiscv cpu) {
		this.cpu = cpu;
	}

	@Override
	public void handleFloatingPointInstruction(int instruction) {
		cpu.triggerException(InstructionLevelRiscv.ExceptionType.ILLEGAL_INSTRUCTION);
	}

}
