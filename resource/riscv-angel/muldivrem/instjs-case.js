

switch(raw & 0x7F) {

	// I-TYPE, opcode: 0b0010011
	case 0x13:
		var funct3 = ((raw >>> 12) & 0x7);
		// ...
		break;

	// R-TYPE, opcode: 0b0110011
	case 0x33:
		var funct10 = (((raw >>> 25) & 0x7F) << 3) | ((raw >>> 12) & 0x7);
		// ...
		break;

	// L-TYPE (LUI) - opcode: 0b0110111
	case 0x37:
		RISCV.gen_reg[((raw >>> 7) & 0x1F)] = signExtLT32_64(((raw & 0xFFFFF000)));
		RISCV.pc += 4;
		break;

	// L-TYPE (AUIPC) - opcode: 0b0010111
	case 0x17:
		RISCV.gen_reg[((raw >>> 7) & 0x1F)] = signExtLT32_64(((raw & 0xFFFFF000)) + (RISCV.pc & 0xFFFFF000));
		if ((RISCV.gen_reg[((raw >>> 7) & 0x1F)].getLowBitsUnsigned() & 0xFF000000) == 0x55000000) {
			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(RISCV.gen_reg[((raw >>> 7) & 0x1F)].getLowBitsUnsigned(), 0x155);
		}
		RISCV.pc += 4;
		break;

	// Loads
	case 0x3:
		var funct3 = ((raw >>> 12) & 0x7);
		// ...
		break;

	// Stores
	case 0x23:
		var funct3 = ((raw >>> 12) & 0x7);
		// ...
		break;

	// FENCE instructions - NOPS for this imp
	case 0x0F:
		var funct3 = ((raw >>> 12) & 0x7);
		if (funct3 == 0x1) {
			// FENCE.I is no-op in this implementation
			RISCV.pc += 4;
		} else if (funct3 == 0x0) {
			// FENCE is no-op in this implementation
			RISCV.pc += 4;
		} else {
			throw new RISCVTrap("Illegal Instruction");
		}
		break;

	// 32 bit integer compute instructions
	case 0x1B:
		var funct3 = ((raw >>> 12) & 0x7);
		// ...
		break;


	// more 32 bit int compute
	case 0x3B:
		var funct10 = (((raw >>> 25) & 0x7F) << 3) | ((raw >>> 12) & 0x7);
		// ...
		break;

	// atomic memory instructions
	case 0x2F:
		var funct8 = ((((raw >>> 25) & 0x7F) >> 2) << 3) | ((raw >>> 12) & 0x7);
		// ...
		break;





	// B-TYPE (Branches) - opcode: 0b1100011
	case 0x63:
		var funct3 = ((raw >>> 12) & 0x7);
		// ...
		break;

	// I-TYPES (JALR)
	case 0x67:
		var funct3 = ((raw >>> 12) & 0x7);
		if (funct3 == 0x0) {
			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = signExtLT32_64(RISCV.pc + 4);
			RISCV.pc = ((raw >> 20)) + (RISCV.gen_reg[((raw >>> 15) & 0x1F)].getLowBits()|0);
		} else {
			throw new RISCVTrap("Illegal Instruction");
		}
		break;

	// J-TYPE (JAL) - opcode: 0b1101111
	case 0x6F:
		RISCV.gen_reg[((raw >>> 7) & 0x1F)] = signExtLT32_64(RISCV.pc + 4);
		RISCV.pc = (RISCV.pc|0) + (((raw >> 20) & 0xFFF007FE) | ((raw >>> 9) & 0x00000800) | (raw & 0x000FF000));
		break;

	// R-TYPES (continued): System instructions
	case 0x73:
		var superfunct = ((raw >>> 12) & 0x7) | ((raw >>> 20) & 0x1F) << 3 | ((raw >>> 25) & 0x7F) << 8;
		// ...
		break;




	/* NOTE ABOUT FP: ALL FP INSTRUCTIONS IN THIS IMPLEMENTATION WILL ALWAYS
	 * THROW THE "Floating-Point Disabled" TRAP.
	 */

	// Floating-Point Memory Insts, FLW, FLD
	case 0x7:
	case 0x27:
	case 0x43:
	case 0x47:
	case 0x4B:
	case 0x4F:
	case 0x53:
		RISCV.excpTrigg = new RISCVTrap("Floating-Point Disabled");
		return;
		break;

	default:
		//throw new RISCVError("Unknown instruction at: 0x" + RISCV.pc.toString(16));
		//don't throw error for completely unknown inst (i.e. unknown opcode)
		throw new RISCVTrap("Illegal Instruction OCCURRED HERE 2");
		break;
}
