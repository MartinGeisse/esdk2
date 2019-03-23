// atomic memory instructions
case 0x2F:
	var funct8 = ((((raw >>> 25) & 0x7F) >> 2) << 3) | ((raw >>> 12) & 0x7);
	switch(funct8) {

		// AMOADD.W
		case 0x2:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.add(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOSWAP.W
		case 0xA:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}
			var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOXOR.W
		case 0x22:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.xor(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOAND.W
		case 0x62:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.and(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOOR.W
		case 0x42:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.or(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOMIN.W
		case 0x82:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			if (rd_temp.greaterThan(RISCV.gen_reg[((raw >>> 20) & 0x1F)])) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;

			RISCV.pc += 4;
			break;

		// AMOMAX.W
		case 0xA2:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			if (rd_temp.lessThan(RISCV.gen_reg[((raw >>> 20) & 0x1F)])) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;


		// AMOMINU.W
		case 0xC2:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			if (signed_to_unsigned(rd_temp.getLowBitsUnsigned()) > signed_to_unsigned(RISCV.gen_reg[((raw >>> 20) & 0x1F)].getLowBitsUnsigned())) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOMAXU.W
		case 0xE2:
			var rd_temp = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}

			if (signed_to_unsigned(rd_temp.getLowBitsUnsigned()) < signed_to_unsigned(RISCV.gen_reg[((raw >>> 20) & 0x1F)].getLowBitsUnsigned())) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp.getLowBitsUnsigned());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;


		// AMOADD.D
		case 0x3:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.add(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOSWAP.D
		case 0xB:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOXOR.D
		case 0x23:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.xor(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOAND.D
		case 0x63:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.and(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOOR.D
		case 0x43:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			var temp = rd_temp.or(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOMIN.D
		case 0x83:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			if (rd_temp.greaterThan(RISCV.gen_reg[((raw >>> 20) & 0x1F)])) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOMAX.D
		case 0xA3:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			if (rd_temp.lessThan(RISCV.gen_reg[((raw >>> 20) & 0x1F)])) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOMINU.D
		case 0xC3:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			if (!long_less_than_unsigned(rd_temp, RISCV.gen_reg[((raw >>> 20) & 0x1F)])) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// AMOMAXU.D
		case 0xE3:
			var rd_temp = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			if (long_less_than_unsigned(rd_temp, RISCV.gen_reg[((raw >>> 20) & 0x1F)])) {
				var temp = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
			} else {
				var temp = rd_temp;
			}
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], temp);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = rd_temp;
			RISCV.pc += 4;
			break;

		// LR.W
		case 0x12:
			// This acts just like a lw in this implementation (no need for sync)
			// (except there's no immediate)
			var fetch = signExtLT32_64(RISCV.load_word_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]));
			if (RISCV.excpTrigg) {
				return;
			}
			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = fetch;

			RISCV.pc += 4;
			break;

		// LR.D
		case 0x13:
			// This acts just like a ld in this implementation (no need for sync)
			// (except there's no immediate)
			var fetch = RISCV.load_double_from_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}
			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = fetch;
			RISCV.pc += 4;
			break;

		// SC.W
		case 0x1A:
			// this acts just like a sd in this implementation, but it will
			// always set the check register to 0 (indicating load success)
			RISCV.store_word_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], RISCV.gen_reg[((raw >>> 20) & 0x1F)].getLowBits());
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = Long.ZERO; // indicate success
			RISCV.pc += 4;
			break;

		// SC.D
		case 0x1B:
			// this acts just like a sd in this implementation, but it will
			// always set the check register to 0 (indicating load success)
			RISCV.store_double_to_mem(RISCV.gen_reg[((raw >>> 15) & 0x1F)], RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
			if (RISCV.excpTrigg) {
				return;
			}

			RISCV.gen_reg[((raw >>> 7) & 0x1F)] = Long.ZERO; // indicate success
			RISCV.pc += 4;
			break;


		default:
			throw new RISCVTrap("Illegal Instruction");
			break;

	}
	break;
