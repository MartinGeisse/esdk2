package name.martingeisse.esdk.riscv.instruction.muldiv;

import name.martingeisse.esdk.riscv.instruction.InstructionLevelRiscv;

/**
 *
 */
public final class ExceptionMultiplyDivideUnit implements MultiplyDivideUnit {

	private final InstructionLevelRiscv cpu;

	public ExceptionMultiplyDivideUnit(InstructionLevelRiscv cpu) {
		this.cpu = cpu;
	}

	@Override
	public void performMultiplayDivideInstruction(int instruction) {
		cpu.triggerException(InstructionLevelRiscv.ExceptionType.ILLEGAL_INSTRUCTION);
	}

}
