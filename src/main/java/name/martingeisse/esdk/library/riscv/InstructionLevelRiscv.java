package name.martingeisse.esdk.library.riscv;

/**
 * Note: Interrupts are not supported for now.
 */
public abstract class InstructionLevelRiscv {

	private final int[] registers = new int[32];
	private int pc;

	/**
	 * Resets the CPU.
	 */
	public void reset() {
		pc = 0;
	}

	/**
	 * Executes a single instruction.
	 */
	public void step() {
		if ((pc & 3) != 0) {
			onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
			return;
		}
		int instruction = fetchInstruction(pc >> 2);
		pc += 4;
		if ((instruction & 3) != 3) {
			onExtendedInstruction(instruction);
			return;
		}
		mainOpcodeSwitch:
		switch ((instruction >> 2) & 31) {

			case 0: { // LOAD
				int widthCode = (instruction >> 12) & 3;
				boolean unsigned = ((instruction >> 12) & 4) != 0;
				int address = getRegister(instruction >> 15) + (instruction >> 20);
				int convertedData;
				switch (widthCode) {

					case 0: // byte TODO little endian!
						convertedData = read(address >> 2) >> ((address & 3) * 8);
						convertedData = (unsigned ? (convertedData & 0xff) : (byte) convertedData);
						break;

					case 1: // half-word TODO little endian!
						if ((address & 1) != 0) {
							onException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						convertedData = read(address >> 2) >> ((address & 1) * 8);
						convertedData = (unsigned ? (convertedData & 0xffff) : (short) convertedData);
						break;

					case 2: // word
						if ((address & 3) != 0) {
							onException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						convertedData = read(address >> 2);
						break;

					default:
						onException(ExceptionType.ILLEGAL_INSTRUCTION);
						break mainOpcodeSwitch;

				}
				setRegister(instruction >> 7, convertedData);
				break;
			}

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
				// TODO old or new pc?
				setRegister(instruction >> 7, (instruction & 0xfffff000) + pc);
				break;

			case 6: // OP-IMM-32
				throw new UnsupportedOperationException("this is a 32-bit implementation -- 32-on-64-bit operations are not supported");

			case 7: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				onExtendedInstruction(instruction);
				break;

			case 8: { // STORE
				int widthCode = (instruction >> 12) & 7;
				int address = getRegister(instruction >> 15) + ((instruction >> 7) & 31) + ((instruction & 0xfe000000) >> 20);
				int wordAddress = address >> 2;
				int data = getRegister(instruction >> 20);
				switch (widthCode) {

					case 0: // byte TODO little endian!
						write(wordAddress, data, 1 << (address & 3));
						break;

					case 1: // half-word TODO little endian!
						if ((address & 1) != 0) {
							onException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						write(wordAddress, data, 3 << (address & 2));
						break;

					case 2: // word
						if ((address & 3) != 0) {
							onException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						write(wordAddress, data, 15);
						break;

					default:
						onException(ExceptionType.ILLEGAL_INSTRUCTION);
						break mainOpcodeSwitch;

				}
				break;
			}

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
				setRegister(instruction >> 7, instruction & 0xfffff000);
				break;

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

			case 24: { // BRANCH
				int operand1 = getRegister(instruction >> 15);
				int operand2 = getRegister(instruction >> 20);
				boolean condition;
				switch ((instruction >> 12) & 7) {

					case 0: // BEQ
						condition = (operand1 == operand2);
						break;

					case 1: // BNE
						condition = (operand1 != operand2);
						break;

					case 4: // BLT
						condition = (operand1 < operand2);
						break;

					case 5: // BGE
						condition = (operand1 >= operand2);
						break;

					case 6: // BLTU
						condition = Integer.compareUnsigned(operand1, operand2) < 0;
						break;

					case 7: // BGEU
						condition = Integer.compareUnsigned(operand1, operand2) >= 0;
						break;

					case 2: // unused
					case 3: // unused
					default:
						onException(ExceptionType.ILLEGAL_INSTRUCTION);
						break mainOpcodeSwitch;

				}
				if (condition) {
					int offset =
						((instruction >> 7) & (2 + 4 + 8 + 16)) +
							((instruction >> 20) & (32 + 64 + 128 + 256 + 512 + 1024)) +
							((instruction << 4) & 2048) +
							((instruction >> 19) & 4096);
					pc += offset;
				}
				break;
			}

			case 25: // JALR
				int baseRegisterValue = getRegister(instruction >> 15);
				setRegister(instruction >> 7, pc);
				if ((instruction & (1 << 21)) != 0) {
					onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
					break;
				}
				pc = (baseRegisterValue + (instruction >> 20)) & -2;
				break;

			case 26: // reserved
				onException(ExceptionType.ILLEGAL_INSTRUCTION);
				break;

			case 27: // JAL
				setRegister(instruction >> 7, pc);
				if ((instruction & (1 << 12)) != 0) {
					onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
					break;
				}
				pc = pc - 4 + (instruction >> 13 << 2);
				break;

			case 28: // SYSTEM
				onException(ExceptionType.SYSTEM_INSTRUCTION);
				break;

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

	public final int getPc() {
		return pc;
	}

	public final void setPc(int pc) {
		if ((pc & 3) != 0) {
			onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
		} else {
			this.pc = pc;
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
		DATA_ADDRESS_MISALIGNED,
		SYSTEM_INSTRUCTION
	}

}
