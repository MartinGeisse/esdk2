package name.martingeisse.esdk.library.riscv.muldiv;

import name.martingeisse.esdk.library.riscv.InstructionLevelRiscv;
import name.martingeisse.esdk.library.riscv.floating.FloatingPointUnit;

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
