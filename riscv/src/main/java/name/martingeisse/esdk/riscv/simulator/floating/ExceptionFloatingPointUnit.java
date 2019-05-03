package name.martingeisse.esdk.riscv.simulator.floating;

import name.martingeisse.esdk.riscv.simulator.InstructionLevelRiscv;

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