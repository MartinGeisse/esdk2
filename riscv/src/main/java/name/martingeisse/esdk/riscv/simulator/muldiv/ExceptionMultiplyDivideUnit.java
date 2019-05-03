package name.martingeisse.esdk.riscv.simulator.muldiv;

import name.martingeisse.esdk.riscv.simulator.InstructionLevelRiscv;
import name.martingeisse.esdk.riscv.floating.FloatingPointUnit;

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
