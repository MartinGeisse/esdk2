
                // MUL
                case 0x8:
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = (RISCV.gen_reg[((raw >>> 15) & 0x1F)]).multiply(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
                    RISCV.pc += 4;
                    break;

                // MULH
                case 0x9:
                    // plan: long -> string -> bignum -> do the mult
                    // then divide by 2^64 (equiv to right shift by 64 bits)
                    // then bignum -> string -> Long.fromString()
                    var big1 = BigInteger(RISCV.gen_reg[((raw >>> 15) & 0x1F)].toString(10));
                    var big2 = BigInteger(RISCV.gen_reg[((raw >>> 20) & 0x1F)].toString(10));
                    var bigres = big1.multiply(big2);
                    var bigdiv = BigInteger("18446744073709551616"); // 2^64
                    var bigresf = bigres.divide(bigdiv);

                    // need to fix one-off error for negative nums when doing this shift
                    if (bigres.isNegative()) {
                        bigresf = bigresf.subtract(BigInteger("1"));
                    }

                    bigresf = bigresf.toString(10);
                    var result = Long.fromString(bigresf, 10);
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = result;
                    RISCV.pc += 4;
                    break;

                // MULHSU
                case 0xA:
                    var l1 = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    var l2 = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
                    var l2neg = (l2.getHighBits() & 0x80000000) != 0;
                    var big1 = BigInteger(l1);

                    if (l2neg) {
                        l2 = new Long(l2.getLowBits(), l2.getHighBits() & 0x7FFFFFFF);
                        var big2 = BigInteger(l2);
                        big2 = big2.add(BigInteger("9223372036854775808")); // 2^63
                    } else {
                        var big2 = BigInteger(l2);
                    }

                    var bigres = big1.multiply(big2);
                    var bigdiv = BigInteger("18446744073709551616"); // 2^64
                    var bigresf = bigres.divide(bigdiv);

                    // need to fix one-off error for negative nums when doing this shift
                    if (bigres.isNegative()) {
                        bigresf = bigresf.subtract(BigInteger("1"));
                    }


                    // now we have the upper 64 bits of result, signed
                    bigresf = bigresf.toString(10);
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = Long.fromString(bigresf, 10);
                    RISCV.pc += 4;
                    break;

                // MULHU
                case 0xB:
                    // plan: long -determine/fix signs -> string -> bignum -> do the mult
                    // then divide by 2^64 (equiv to right shift by 64 bits)
                    // then bignum -> string -> Long.fromString()
                    var l1 = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    var l2 = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
                    var l1neg = (l1.getHighBits() & 0x80000000) != 0;
                    var l2neg = (l2.getHighBits() & 0x80000000) != 0;
                    if (l1neg) {
                        l1 = new Long(l1.getLowBits(), l1.getHighBits() & 0x7FFFFFFF);
                        var big1 = BigInteger(l1);
                        big1 = big1.add(BigInteger("9223372036854775808"));
                    } else {
                        var big1 = BigInteger(l1);
                    }
                    if (l2neg) {
                        l2 = new Long(l2.getLowBits(), l2.getHighBits() & 0x7FFFFFFF);
                        var big2 = BigInteger(l2);
                        big2 = big2.add(BigInteger("9223372036854775808")); // 2^63
                    } else {
                        var big2 = BigInteger(l2);
                    }

                    var bigres = big1.multiply(big2);
                    var bigdiv = BigInteger("18446744073709551616"); // 2^64
                    var bigresf = bigres.divide(bigdiv);
                    var bigsub = BigInteger("9223372036854775808"); // 2^63
                    if (bigresf.compare(bigsub) >= 0) {
                        // need to subtract bigsub, manually set MSB
                        bigresf = bigresf.subtract(bigsub);
                        bigresf = bigresf.toString(10)
                        var res = Long.fromString(bigresf, 10);
                        res = new Long(res.getLowBits(), res.getHighBits()|0x80000000);
                    } else {
                        bigresf = bigresf.toString(10);
                        var res = Long.fromString(bigresf, 10);
                    }
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = res;
                    RISCV.pc += 4;
                    break;

                // DIV 
                case 0xC:
                    if (RISCV.gen_reg[((raw >>> 20) & 0x1F)].isZero()) {
                        // divide by zero, result is all ones
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(0xFFFFFFFF, 0xFFFFFFFF);
                    } else if (RISCV.gen_reg[((raw >>> 15) & 0x1F)].equals(new Long(0x0, 0x80000000)) && RISCV.gen_reg[((raw >>> 15) & 0x1F)].equals(new Long(0xFFFFFFFF, 0xFFFFFFFF))) {
                        // divide most negative num by -1 -> signed overflow
                        // set result to dividend
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    } else {
                        // actual division
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = RISCV.gen_reg[((raw >>> 15) & 0x1F)].div(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
                    }
                    RISCV.pc += 4;
                    break;

                // DIVU
                case 0xD:
                    var l1 = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    var l2 = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
                    if (l2.isZero()) {
                        //div by zero
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(0xFFFFFFFF, 0xFFFFFFFF);
                        RISCV.pc += 4;
                        break;
                    }

                    var l1neg = (l1.getHighBits() & 0x80000000) != 0;
                    var l2neg = (l2.getHighBits() & 0x80000000) != 0;
                    if (l1neg) {
                        l1 = new Long(l1.getLowBits(), l1.getHighBits() & 0x7FFFFFFF);
                        var big1 = BigInteger(l1);
                        big1 = big1.add(BigInteger("9223372036854775808"));
                    } else {
                        var big1 = BigInteger(l1);
                    }
                    if (l2neg) {
                        l2 = new Long(l2.getLowBits(), l2.getHighBits() & 0x7FFFFFFF);
                        var big2 = BigInteger(l2);
                        big2 = big2.add(BigInteger("9223372036854775808")); // 2^63
                    } else {
                        var big2 = BigInteger(l2);
                    }

                    var bigresf = big1.divide(big2);
                    var bigsub = BigInteger("9223372036854775808"); // 2^63
                    if (bigresf.compare(bigsub) >= 0) {
                        // need to subtract bigsub, manually set MSB
                        bigresf = bigresf.subtract(bigsub);
                        bigresf = bigresf.toString(10)
                        var res = Long.fromString(bigresf, 10);
                        res = new Long(res.getLowBits(), res.getHighBits()|0x80000000);
                    } else {
                        bigresf = bigresf.toString(10);
                        var res = Long.fromString(bigresf, 10);
                    }
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = res;
                    RISCV.pc += 4;
                    break;

                // REM
                case 0xE:
                    if (RISCV.gen_reg[((raw >>> 20) & 0x1F)].isZero()) {
                        // rem (divide) by zero, result is dividend
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    } else if (RISCV.gen_reg[((raw >>> 15) & 0x1F)].equals(new Long(0x0, 0x80000000)) && RISCV.gen_reg[((raw >>> 15) & 0x1F)].equals(new Long(0xFFFFFFFF, 0xFFFFFFFF))) {
                        // rem (divide) most negative num by -1 -> signed overflow
                        // set result to dividend
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = Long.ZERO;
                    } else {
                        // actual rem
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = RISCV.gen_reg[((raw >>> 15) & 0x1F)].modulo(RISCV.gen_reg[((raw >>> 20) & 0x1F)]);
                    }
                    RISCV.pc += 4;
                    break;

                // REMU
                case 0xF:
                    var l1 = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    var l2 = RISCV.gen_reg[((raw >>> 20) & 0x1F)];
                    if (l2.isZero()) {
                        //div by zero
                        RISCV.gen_reg[((raw >>> 7) & 0x1F)] = l1;
                        RISCV.pc += 4;
                        break;
                    }

                    var l1neg = (l1.getHighBits() & 0x80000000) != 0;
                    var l2neg = (l2.getHighBits() & 0x80000000) != 0;
                    if (l1neg) {
                        l1 = new Long(l1.getLowBits(), l1.getHighBits() & 0x7FFFFFFF);
                        var big1 = BigInteger(l1);
                        big1 = big1.add(BigInteger("9223372036854775808"));
                    } else {
                        var big1 = BigInteger(l1);
                    }
                    if (l2neg) {
                        l2 = new Long(l2.getLowBits(), l2.getHighBits() & 0x7FFFFFFF);
                        var big2 = BigInteger(l2);
                        big2 = big2.add(BigInteger("9223372036854775808")); // 2^63
                    } else {
                        var big2 = BigInteger(l2);
                    }

                    var bigresf = big1.remainder(big2);
                    var bigsub = BigInteger("9223372036854775808"); // 2^63
                    if (bigresf.compare(bigsub) >= 0) {
                        // need to subtract bigsub, manually set MSB
                        bigresf = bigresf.subtract(bigsub);
                        bigresf = bigresf.toString(10)
                        var res = Long.fromString(bigresf, 10);
                        res = new Long(res.getLowBits(), res.getHighBits()|0x80000000);
                    } else {
                        bigresf = bigresf.toString(10);
                        var res = Long.fromString(bigresf, 10);
                    }
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = res;
                    RISCV.pc += 4;
                    break;
