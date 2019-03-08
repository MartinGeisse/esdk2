package name.martingeisse.esdk.library.riscv;

/**
 * Note: Interrupts are not supported for now.
 *
 * TODO is "pcInWords" really a good idea? Might be slightly faster but it'S really hard to understand while reading the code
 */
public abstract class InstructionLevelRiscv {

	private final int[] registers = new int[32];
	private int pcInWords;

	/**
	 * Resets the CPU.
	 */
	public void reset() {
		pcInWords = 0;
	}

	/**
	 * Executes a single instruction.
	 */
	public void step() {
		int instruction = fetchInstruction(pcInWords);
		pcInWords++;
		if ((instruction & 3) != 3) {
			onExtendedInstruction(instruction);
			return;
		}
		switch ((instruction >> 2) & 31) {

			case 0: // LOAD
				throw new UnsupportedOperationException("not yet implemented"); // TODO

			case 1: // LOAD-FP
				onFloatingPointInstruction(instruction);
				break;

			case 2: // custopm-0
				onExtendedInstruction(instruction);
				break;

			case 3: // MISC-MEM, i.e. FENCE and FENCE.I -- implemented as NOPs
				break;

			case 4: // OP-IMM
				performOperation(instruction, instruction >> 20); // TODO
				break;

			case 5: // AUIPC
				throw new UnsupportedOperationException("not yet implemented"); // TODO

			case 6: // OP-IMM-32
				throw new UnsupportedOperationException("this is a 32-bit implementation -- 32-on-64-bit operations are not supported");

			case 7: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				onExtendedInstruction(instruction);
				break;

			case 8: // STORE
				throw new UnsupportedOperationException("not yet implemented"); // TODO

			case 9: // STORE-FP
				onFloatingPointInstruction(instruction);
				break;

			case 10: // custom-1
				onExtendedInstruction(instruction);
				break;

			case 11: // AMO (atomic memory operation)
				throw new UnsupportedOperationException("AMO not supported by this implementation");

			case 12: // OP
				performOperation(instruction, getRegister(instruction >> 20)); // TODO
				break;

			case 13: // LUI
				throw new UnsupportedOperationException("not yet implemented"); // TODO

			case 14: // OP-32
				throw new UnsupportedOperationException("this is a 32-bit implementation -- 32-on-64-bit operations are not supported");

			case 15: // reserved for 64-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				onExtendedInstruction(instruction);
				break;

			case 16: // MADD
				onFloatingPointInstruction(instruction);
				break;

			case 17: // MSUB
				onFloatingPointInstruction(instruction);
				break;

			case 18: // NMSUB
				onFloatingPointInstruction(instruction);
				break;

			case 19: // NMADD
				onFloatingPointInstruction(instruction);
				break;

			case 20: // OP-FP
				onFloatingPointInstruction(instruction);
				break;

			case 21: // reserved
				onException(ExceptionType.ILLEGAL_INSTRUCTION);
				break;

			case 22: // custom-2
				onExtendedInstruction(instruction);
				break;

			case 23: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				onExtendedInstruction(instruction);
				break;

			case 24: // BRANCH
				throw new UnsupportedOperationException("not yet implemented"); // TODO

			case 25: // JALR
				int baseRegisterValue = getRegister(instruction >> 15);
				setRegister(instruction >> 7, getPc());
				if ((instruction & (1 << 21)) != 0) {
					onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
					break;
				}
				pcInWords = (baseRegisterValue >> 2) + (instruction >> 22);
				break;

			case 26: // reserved
				onException(ExceptionType.ILLEGAL_INSTRUCTION);
				break;

			case 27: // JAL
				setRegister(instruction >> 7, getPc());
				if ((instruction & (1 << 12)) != 0) {
					onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
					break;
				}
				pcInWords = pcInWords - 1 + (instruction >> 13);
				break;

			case 28: // SYSTEM
				throw new UnsupportedOperationException("not yet implemented"); // TODO

			case 29: // reserved
				onException(ExceptionType.ILLEGAL_INSTRUCTION);
				break;

			case 30: // custom-3
				onExtendedInstruction(instruction);
				break;

			case 31: // reserved for 80-bit+ instructions, but we only use 32-bit instructions, so this is free for custom instructions
				onExtendedInstruction(instruction);
				break;

		}
	}

	private void performOperation(int instruction, int rightOperand) {
		// TODO
	}

	// ----------------------------------------------------------------------------------------------------------------
	// PC
	// ----------------------------------------------------------------------------------------------------------------

	public final int getPcInWords() {
		return pcInWords;
	}

	public final void setPcInWords(int pcInWords) {
		this.pcInWords = pcInWords;
	}

	public final int getPc() {
		return pcInWords << 2;
	}

	public final void setPc(int pc) {
		if ((pc & 3) != 0) {
			onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
		} else {
			this.pcInWords = pc >> 2;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// general-purpose registers
	// ----------------------------------------------------------------------------------------------------------------

	public final int getRegister(int index) {
		checkRegisterIndex(index);
		return registers[index];
	}

	public final void setRegister(int index, int value) {
		checkRegisterIndex(index);
		if (index != 0) {
			registers[index] = value;
		}
	}

	private void checkRegisterIndex(int index) {
		if (index < 0 || index >= 32) {
			throw new RuntimeException("invalid register index: " + index);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// abstract behavior
	// ----------------------------------------------------------------------------------------------------------------

	protected abstract int fetchInstruction(int wordAddress);
	protected abstract int read(int wordAddress);
	protected abstract void write(int wordAddress, int data, int mask);

	protected void onException(ExceptionType type) {
		throw new RuntimeException("RISC-V cpu exception: " + type);
	}

	protected void onExtendedInstruction(int instruction) {
		onException(ExceptionType.ILLEGAL_INSTRUCTION);
	}

	protected void onFloatingPointInstruction(int instruction) {
		onException(ExceptionType.ILLEGAL_INSTRUCTION);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// helper classes
	// ----------------------------------------------------------------------------------------------------------------

	public enum ExceptionType {
		INSTRUCTION_ADDRESS_MISALIGNED,
		ILLEGAL_INSTRUCTION,
	}

}
