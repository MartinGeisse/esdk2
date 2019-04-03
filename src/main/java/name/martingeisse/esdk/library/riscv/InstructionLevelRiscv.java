package name.martingeisse.esdk.library.riscv;

import name.martingeisse.esdk.library.riscv.extended.ExceptionExtendedInstructionUnit;
import name.martingeisse.esdk.library.riscv.extended.ExtendedInstructionUnit;
import name.martingeisse.esdk.library.riscv.floating.ExceptionFloatingPointUnit;
import name.martingeisse.esdk.library.riscv.floating.FloatingPointUnit;
import name.martingeisse.esdk.library.riscv.io.BrokenIoUnit;
import name.martingeisse.esdk.library.riscv.io.IoUnit;
import name.martingeisse.esdk.library.riscv.muldiv.ExceptionMultiplyDivideUnit;
import name.martingeisse.esdk.library.riscv.muldiv.MultiplyDivideUnit;

/**
 * Note: Interrupts are not supported for now.
 * <p>
 * This implementation is little-endian only, which in a word-based addressing scheme means: For an aligned 4-byte block
 * (i.e. one addressing word), accessing the lowest byte accesses the 8 bits with lowest significance in the 32-bit value.
 */
public abstract class InstructionLevelRiscv {

	private IoUnit ioUnit;
	private MultiplyDivideUnit multiplyDivideUnit;
	private FloatingPointUnit floatingPointUnit;
	private ExtendedInstructionUnit extendedInstructionUnit;
	private boolean supportsMisalignedIo;

	private final int[] registers = new int[32];
	private int pc;

	public InstructionLevelRiscv() {
		setIoUnit(null);
		setMultiplyDivideUnit(null);
		setFloatingPointUnit(null);
		setExtendedInstructionUnit(null);
		setSupportsMisalignedIo(false);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// configuration
	// ----------------------------------------------------------------------------------------------------------------

	public IoUnit getIoUnit() {
		return ioUnit;
	}

	public void setIoUnit(IoUnit ioUnit) {
		this.ioUnit = (ioUnit == null ? BrokenIoUnit.INSTANCE : ioUnit);
	}

	public MultiplyDivideUnit getMultiplyDivideUnit() {
		return multiplyDivideUnit;
	}

	public void setMultiplyDivideUnit(MultiplyDivideUnit multiplyDivideUnit) {
		this.multiplyDivideUnit = (multiplyDivideUnit == null ? new ExceptionMultiplyDivideUnit(this) : multiplyDivideUnit);
	}

	public FloatingPointUnit getFloatingPointUnit() {
		return floatingPointUnit;
	}

	public void setFloatingPointUnit(FloatingPointUnit floatingPointUnit) {
		this.floatingPointUnit = (floatingPointUnit == null ? new ExceptionFloatingPointUnit(this) : floatingPointUnit);
	}

	public ExtendedInstructionUnit getExtendedInstructionUnit() {
		return extendedInstructionUnit;
	}

	public void setExtendedInstructionUnit(ExtendedInstructionUnit extendedInstructionUnit) {
		this.extendedInstructionUnit = (extendedInstructionUnit == null ? new ExceptionExtendedInstructionUnit(this) : extendedInstructionUnit);
	}

	public boolean isSupportsMisalignedIo() {
		return supportsMisalignedIo;
	}

	public void setSupportsMisalignedIo(boolean supportsMisalignedIo) {
		this.supportsMisalignedIo = supportsMisalignedIo;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// operation
	// ----------------------------------------------------------------------------------------------------------------

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
			triggerException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
			return;
		}
		int instruction = ioUnit.fetchInstruction(pc >> 2);
		int oldPc = pc;
		pc += 4;
		if ((instruction & 3) != 3) {
			extendedInstructionUnit.handleExtendedInstruction(instruction);
			return;
		}
		mainOpcodeSwitch:
		switch ((instruction >> 2) & 31) {

			case 0: { // LOAD
				int widthCode = (instruction >> 12) & 3;
				boolean unsigned = ((instruction >> 12) & 4) != 0;
				int address = getRegister(instruction >> 15) + (instruction >> 20);
				int wordAddress = (address >> 2);
				int convertedData;
				switch (widthCode) {

					case 0: // byte
						convertedData = ioUnit.read(wordAddress) >> ((address & 3) * 8);
						convertedData = (unsigned ? (convertedData & 0xff) : (byte) convertedData);
						break;

					case 1: { // half-word
						int word = ioUnit.read(wordAddress);
						int shiftedData;
						switch (address & 3) {

							case 0:
								shiftedData = word;
								break;

							case 1:
								if (supportsMisalignedIo) {
									shiftedData = word >> 8;
								} else {
									triggerException(ExceptionType.DATA_ADDRESS_MISALIGNED);
									break mainOpcodeSwitch;
								}
								break;

							case 2:
								shiftedData = word >> 16;
								break;

							case 3:
								if (supportsMisalignedIo) {
									shiftedData = (word >>> 24) | (ioUnit.read(wordAddress + 1) << 8);
								} else {
									triggerException(ExceptionType.DATA_ADDRESS_MISALIGNED);
									break mainOpcodeSwitch;
								}
								break;

							default:
								throw new RuntimeException("wtf");

						}
						convertedData = (unsigned ? (shiftedData & 0xffff) : (short) shiftedData);
						break;
					}

					case 2: { // word
						if (unsigned) {
							triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
							break mainOpcodeSwitch;
						}
						int lowBits = (address & 3);
						if (lowBits == 0) {
							convertedData = ioUnit.read(wordAddress);
						} else if (supportsMisalignedIo) {
							int shift = lowBits << 3;
							int part1 = ioUnit.read(wordAddress) >>> shift;
							int part2 = ioUnit.read(wordAddress + 1) << (32 - shift);
							convertedData = part1 | part2;
						} else {
							triggerException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						break;
					}

					default:
						triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
						break mainOpcodeSwitch;

				}
				setRegister(instruction >> 7, convertedData);
				break;
			}

			case 1: // LOAD-FP
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 2: // custom-0
				extendedInstructionUnit.handleExtendedInstruction(instruction);
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
				extendedInstructionUnit.handleExtendedInstruction(instruction);
				break;

			case 8: { // STORE
				int widthCode = (instruction >> 12) & 7;
				int address = getRegister(instruction >> 15) + ((instruction >> 7) & 31) + ((instruction & 0xfe000000) >> 20);
				int wordAddress = address >> 2;
				int data = getRegister(instruction >> 20);
				switch (widthCode) {

					case 0: { // byte
						int byteOffset = (address & 3);
						ioUnit.write(wordAddress, data << (byteOffset * 8), 1 << byteOffset);
						break;
					}

					case 1: { // half-word
						switch (address & 3) {

							case 0:
								ioUnit.write(wordAddress, data, 3);
								break;

							case 1:
								if (supportsMisalignedIo) {
									ioUnit.write(wordAddress, data, 6);
								} else {
									triggerException(ExceptionType.DATA_ADDRESS_MISALIGNED);
									break mainOpcodeSwitch;
								}
								break;

							case 2:
								ioUnit.write(wordAddress, data << 16, 12);
								break;

							case 3:
								if (supportsMisalignedIo) {
									ioUnit.write(wordAddress, data << 24, 8);
									ioUnit.write(wordAddress + 1, data >> 8, 1);
								} else {
									triggerException(ExceptionType.DATA_ADDRESS_MISALIGNED);
									break mainOpcodeSwitch;
								}
								break;

							default:
								throw new RuntimeException("wtf");

						}
						break;
					}

					case 2: { // word
						int lowBits = (address & 3);
						if (lowBits != 0 && !supportsMisalignedIo) {
							triggerException(ExceptionType.DATA_ADDRESS_MISALIGNED);
							break mainOpcodeSwitch;
						}
						switch (lowBits) {

							case 0:
								ioUnit.write(wordAddress, data, 15);
								break;

							case 1:
								ioUnit.write(wordAddress, data << 8, 14);
								ioUnit.write(wordAddress + 1, data >>> 24, 1);
								break;

							case 2:
								ioUnit.write(wordAddress, data << 16, 12);
								ioUnit.write(wordAddress + 1, data >>> 16, 3);
								break;

							case 3:
								ioUnit.write(wordAddress, data << 24, 8);
								ioUnit.write(wordAddress + 1, data >>> 8, 7);

							default:
								throw new RuntimeException("wtf");

						}
						break;
					}

					default:
						triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
						break mainOpcodeSwitch;

				}
				break;
			}

			case 9: // STORE-FP
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 10: // custom-1
				extendedInstructionUnit.handleExtendedInstruction(instruction);
				break;

			case 11: // AMO (atomic memory operation)
				throw new UnsupportedOperationException("AMO not supported by this implementation");

			case 12: // OP
				if (instruction >>> 25 == 1) {
					multiplyDivideUnit.performMultiplayDivideInstruction(instruction);
				} else {
					performOperation(instruction, false);
				}
				break;

			case 13: // LUI
				setRegister(instruction >> 7, instruction & 0xfffff000);
				break;

			case 14: // OP-32
				throw new UnsupportedOperationException("this is a 32-bit implementation -- 32-on-64-bit operations are not supported");

			case 15: // reserved for 64-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				extendedInstructionUnit.handleExtendedInstruction(instruction);
				break;

			case 16: // MADD
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 17: // MSUB
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 18: // NMSUB
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 19: // NMADD
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 20: // OP-FP
				floatingPointUnit.handleFloatingPointInstruction(instruction);
				break;

			case 21: // reserved
				triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
				break;

			case 22: // custom-2
				extendedInstructionUnit.handleExtendedInstruction(instruction);
				break;

			case 23: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				extendedInstructionUnit.handleExtendedInstruction(instruction);
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
						triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
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
				pc = (baseRegisterValue + (instruction >> 20)) & -2;
				break;

			case 26: // reserved
				triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
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
				triggerException(ExceptionType.SYSTEM_INSTRUCTION);
				break;

			case 29: // reserved
				triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
				break;

			case 30: // custom-3
				extendedInstructionUnit.handleExtendedInstruction(instruction);
				break;

			case 31: // reserved for 80-bit+ instructions, but we only use 32-bit instructions, so this is free for custom instructions
				extendedInstructionUnit.handleExtendedInstruction(instruction);
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
				triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
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
	// misc. behavior
	// ----------------------------------------------------------------------------------------------------------------

	public void triggerException(ExceptionType type) {
		throw new RuntimeException("RISC-V cpu exception: " + type + " at pc = " + pc);
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
