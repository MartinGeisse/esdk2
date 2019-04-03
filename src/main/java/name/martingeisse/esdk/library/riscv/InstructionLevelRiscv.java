package name.martingeisse.esdk.library.riscv;

/**
 * Note: Interrupts are not supported for now.
 * <p>
 * This implementation is little-endian only, which in a word-based addressing scheme means: For an aligned 4-byte block
 * (i.e. one addressing word), accessing the lowest byte accesses the 8 bits with lowest significance in the 32-bit value.
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
		int oldPc = pc;
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

					case 0: // byte
						convertedData = read(address >> 2) >> ((address & 3) * 8);
						convertedData = (unsigned ? (convertedData & 0xff) : (byte) convertedData);
						break;

					case 1: // half-word
						if ((address & 1) != 0) {
							onException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						convertedData = read(address >> 2) >> ((address & 2) * 8);
						convertedData = (unsigned ? (convertedData & 0xffff) : (short) convertedData);
						break;

					case 2: // word
						if (unsigned) {
							onException(ExceptionType.ILLEGAL_INSTRUCTION);
							break mainOpcodeSwitch;
						}
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

			case 2: // custom-0
				onExtendedInstruction(instruction);
				break;

			case 3: // MISC-MEM, i.e. FENCE and FENCE.I -- implemented as NOPs
				break;

			case 4: // OP-IMM
				performOperation(instruction, true);
				break;

			case 5: // AUIPC
				setRegister(instruction >> 7, (instruction & 0xfffff000) + oldPc);
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

					case 0: { // byte
						int byteOffset = (address & 3);
						write(wordAddress, data << (byteOffset * 8), 1 << byteOffset);
						break;
					}

					case 1: { // half-word
						if ((address & 1) != 0) {
							onException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						int halfwordOffset = (address & 2);
						write(wordAddress, data << (halfwordOffset * 8), 3 << halfwordOffset);
						break;
					}

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
				performOperation(instruction, false);
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
						condition = (operand1 + Integer.MIN_VALUE < operand2 + Integer.MIN_VALUE);
						break;

					case 7: // BGEU
						condition = (operand1 + Integer.MIN_VALUE >= operand2 + Integer.MIN_VALUE);
						break;

					case 2: // unused
					case 3: // unused
					default:
						onException(ExceptionType.ILLEGAL_INSTRUCTION);
						break mainOpcodeSwitch;

				}
				if (condition) {
					// optimization note: Angel shifts the last component by 20, not 19, then masks out that extra copy
					// of the sign bit. It uses this to merge this shift with the other shift-by-20.
					int offset =
						((instruction >> 7) & 0x0000001e) +
							((instruction >> 20) & 0x000007e0) +
							((instruction << 4) & 0x00000800) +
							((instruction >> 19) & 0xfffff000);
					pc = oldPc + offset;
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

			case 27: { // JAL
				// instruction = imm[20], imm[10:1], imm[11], imm[19:12], rd[4:0], opcode[6:0]; implicitly imm[0] = 0
				setRegister(instruction >> 7, pc);
				int offset = ((instruction >> 11) & 0xfff00000) |
					(instruction & 0x000ff000) |
					((instruction >> 9) & 0x00000800) |
					((instruction >> 20) & 0x7fe);
				pc = oldPc + offset;
				break;
			}

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

	private void performOperation(int instruction, boolean immediate) {

		// decode
		int x = getRegister(instruction >> 15);
		int y = instruction >> 20;
		int func = (instruction >> 12) & 7;
		boolean checkUpperBits, allowExtraBit;
		if (immediate) {
			checkUpperBits = (func == 1 || func == 5);
			allowExtraBit = (func == 5);
		} else {
			y = getRegister(y);
			checkUpperBits = true;
			allowExtraBit = (func == 0 || func == 5);
		}
		if (checkUpperBits) {
			int upperBits = instruction >>> 25;
			if (upperBits != 0 && (upperBits != 32 || !allowExtraBit)) {
				onException(ExceptionType.ILLEGAL_INSTRUCTION);
				return;
			}
		}
		boolean extraBit = allowExtraBit && ((instruction & 0x4000_0000) != 0);

		// execute
		int result;
		switch (func) {

			case 0: // ADD, SUB
				result = extraBit ? (x - y) : (x + y);
				break;

			case 1: // SLL
				result = (x << y);
				break;

			case 2: // SLT
				result = (x < y ? 1 : 0);
				break;

			case 3: // SLTU
				// Explanation why this is correct: Adding MIN_VALUE flips the highest bit. If the highest bits of x
				// and y are equal before flipping, then they are equal afterwards, and the comparison is not changed,
				// so it is a comparison in the lower 31 bits only, which is the same for signed / unsigned.
				// If the highest bits of x and y differ, one of them is greater based on that bit alone, and flipping
				// followed by signed comparison can easily be shown to result in unsigned comparison.
				result = (x + Integer.MIN_VALUE < y + Integer.MIN_VALUE ? 1 : 0);
				break;

			case 4: // XOR
				result = (x ^ y);
				break;

			case 5: // SRL, SRA
				result = extraBit ? (x >> y) : (x >>> y);
				break;

			case 6: // OR
				result = (x | y);
				break;

			case 7: // AND
				result = (x & y);
				break;

			default:
				throw new RuntimeException("this should not happen");

		}
		setRegister(instruction >> 7, result);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// PC
	// ----------------------------------------------------------------------------------------------------------------

	public final int getPc() {
		return pc;
	}

	public final void setPc(int pc) {
		this.pc = pc;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// general-purpose registers
	// ----------------------------------------------------------------------------------------------------------------

	public final int getRegister(int index) {
		index = index & 31;
		return registers[index];
	}

	public final void setRegister(int index, int value) {
		index = index & 31;
		if (index != 0) {
			registers[index] = value;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// abstract behavior
	// ----------------------------------------------------------------------------------------------------------------

	public abstract int fetchInstruction(int wordAddress);

	public abstract int read(int wordAddress);

	public abstract void write(int wordAddress, int data, int byteMask);

	protected void onException(ExceptionType type) {
		throw new RuntimeException("RISC-V cpu exception: " + type + " at pc = " + pc);
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
