/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.tools;

import name.martingeisse.esdk.library.util.StringUtil;

/**
 *
 */
public final class InstructionDisassembler {

	// prevent instantiation
	private InstructionDisassembler() {
	}

	public static String disassemble(int instruction) {
		if ((instruction & 3) != 3) {
			return StringUtil.toHexString32(instruction) + ": cannot decode compact-coded instruction";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(StringUtil.toHexString32(instruction)).append(": opcode = ");
		int mainOpcode = (instruction >> 2) & 31;
		builder.append(Integer.toBinaryString(mainOpcode)).append(" ");
		switch (mainOpcode) {

			case 0: { // LOAD
				int destinationIndex = (instruction >> 7) & 31;
				int addressIndex = (instruction >> 15) & 31;
				int addressImmediate = (instruction >> 20);
				int widthCode = (instruction >> 12) & 3;
				boolean unsigned = ((instruction >> 12) & 4) != 0;
				builder.append("LOAD type, width Code = ").append(widthCode)
					.append(", unsigned = ").append(unsigned);
				fillToAssembler(builder);
				builder.append(widthCode == 0 ? (unsigned ? "lbu" : "lb") :
					widthCode == 1 ? (unsigned ? "lhu" : "lh") : widthCode == 2 ? (unsigned ? "l?" : "lw") : "l?");
				builder.append(" x").append(destinationIndex).append(", ")
					.append(addressImmediate).append("(x").append(addressIndex).append(')');
				break;
			}

			case 1: // LOAD-FP
				unknownOpcode(builder, "loadfp");
				break;

			case 2: // custom-0
				unknownOpcode(builder, "custom0");
				break;

			case 3: // MISC-MEM, i.e. FENCE and FENCE.I -- implemented as NOPs
				unknownOpcode(builder, "MISC-MEM (fence, fence.i, ...)");
				break;

			case 4: // OP-IMM
				disassembleOperation(builder, instruction, true);
				break;

			case 5: // AUIPC
				fillToAssembler(builder);
				builder.append("auipc x").append((instruction >> 7) & 31).append(", ").append(StringUtil.toHexString32(instruction >>> 12));
				break;

			case 6: // OP-IMM-32
				unknownOpcode(builder, "OPI32");
				break;

			case 7: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				builder.append("cannot decode 48-bit instruction");
				break;

			case 8: { // STORE
				int addressIndex = (instruction >> 15) & 31;
				int addressImmediate = ((instruction >> 7) & 31) + ((instruction & 0xfe000000) >> 20);
				int sourceIndex = (instruction >> 20) & 31;
				int widthCode = (instruction >> 12) & 7;
				builder.append("STORE type, width Code = ").append(widthCode);
				fillToAssembler(builder);
				builder.append(widthCode == 0 ? "sb" : widthCode == 1 ? "sh" : widthCode == 2 ? "sw" : "s?");
				builder.append(" x").append(sourceIndex).append(", ")
					.append(addressImmediate).append("(x").append(addressIndex).append(')');
				break;
			}

			case 9: // STORE-FP
				unknownOpcode(builder, "storefp");
				break;

			case 10: // custom-1
				unknownOpcode(builder, "custom1");
				break;

			case 11: // AMO (atomic memory operation)
				unknownOpcode(builder, "AMO*");
				break;

			case 12: { // OP
				disassembleOperation(builder, instruction, false);
				break;
			}

			case 13: // LUI
				fillToAssembler(builder);
				builder.append("lui x").append((instruction >> 7) & 31).append(", ").append(StringUtil.toHexString32(instruction >>> 12));
				break;

			case 14: // OP-32
				unknownOpcode(builder, "OP32");
				break;

			case 15: // reserved for 64-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				builder.append("cannot decode 64-bit instruction");
				break;

			case 16: // MADD
				unknownOpcode(builder, "madd");
				break;

			case 17: // MSUB
				unknownOpcode(builder, "msub");
				break;

			case 18: // NMSUB
				unknownOpcode(builder, "nmsub");
				break;

			case 19: // NMADD
				unknownOpcode(builder, "nmadd");
				break;

			case 20: // OP-FP
				unknownOpcode(builder, "OP-FP");
				break;

			case 21: // reserved
				unknownOpcode(builder, "reserved");
				break;

			case 22: // custom-2
				unknownOpcode(builder, "custom2");
				break;

			case 23: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
				builder.append("cannot decode 48-bit instruction");
				break;

			case 24: { // BRANCH
				unknownOpcode(builder, "BRANCH");
				/*
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
				*/
				break;
			}

			case 25: { // JALR
				int jumpTargetIndex = (instruction >> 15) & 31;
				int destinationIndex = (instruction >> 7) & 31;
				int offset = (instruction >> 20);
				fillToAssembler(builder);
				builder.append("JALR x" + destinationIndex + ", x" + jumpTargetIndex + ", " + offset);
				break;
			}

			case 26: // reserved
				unknownOpcode(builder, "reserved");
				break;

			case 27: { // JAL
				int destinationIndex = (instruction >> 7) & 31;
				int offset = ((instruction >> 11) & 0xfff00000) |
					(instruction & 0x000ff000) |
					((instruction >> 9) & 0x00000800) |
					((instruction >> 20) & 0x7fe);
				fillToAssembler(builder);
				builder.append("JAL " + destinationIndex + ", pc + " + offset);
				break;
			}

			case 28: // SYSTEM
				unknownOpcode(builder, "SYSTEM*");
				break;

			case 29: // reserved
				unknownOpcode(builder, "reserved");
				break;

			case 30: // custom-3
				unknownOpcode(builder, "custom3");
				break;

			case 31: // reserved for 80-bit+ instructions, but we only use 32-bit instructions, so this is free for custom instructions
				builder.append("cannot decode 80-bit instruction");
				break;

			default:
				builder.append("???");
				break;

		}
		return builder.toString();
	}

	private static void unknownOpcode(StringBuilder builder, String description) {
		fillToAssembler(builder);
		builder.append(description).append(" ???");
	}

	private static void fillToAssembler(StringBuilder builder) {
		fillTo(builder, 100);
	}

	private static void fillTo(StringBuilder builder, int to) {
		while (builder.length() < to) {
			builder.append(' ');
		}
	}

	private static void disassembleOperation(StringBuilder builder, int instruction, boolean immediate) {

		// decode
		int func = (instruction >> 12) & 7;
		builder.append(immediate ? "OP-IMM" : "OP").append(", func = ").append(func);
		boolean checkUpperBits, allowExtraBit;
		if (immediate) {
			checkUpperBits = (func == 1 || func == 5);
			allowExtraBit = (func == 5);
		} else {
			checkUpperBits = true;
			allowExtraBit = (func == 0 || func == 5);
		}
		if (checkUpperBits) {
			int upperBits = instruction >>> 25;
			if (upperBits != 0 && (upperBits != 32 || !allowExtraBit)) {
				unknownOpcode(builder, "(invalid upper bits)");
				return;
			}
		}
		boolean extraBit = allowExtraBit && ((instruction & 0x4000_0000) != 0);
		fillToAssembler(builder);

		// execute
		switch (func) {

			case 0: // ADD, SUB
				builder.append(extraBit ? "SUB" : "ADD");
				break;

			case 1: // SLL
				builder.append("SLL");
				break;

			case 2: // SLT
				builder.append("SLT");
				break;

			case 3: // SLTU
				builder.append("SLTU");
				break;

			case 4: // XOR
				builder.append("XOR");
				break;

			case 5: // SRL, SRA
				builder.append(extraBit ? "SRA" : "SRL");
				break;

			case 6: // OR
				builder.append("OR");
				break;

			case 7: // AND
				builder.append("AND");
				break;

			default:
				builder.append("???");
				break;

		}
		builder.append(immediate ? "I x" : " x").append((instruction >> 7) & 31).append(", x")
			.append((instruction >> 15) & 31);
		if (immediate) {
			builder.append(", ").append(StringUtil.toHexString32(instruction >>> 20));
		} else {
			builder.append(", x").append((instruction >> 20) & 31);
		}
	}

}
