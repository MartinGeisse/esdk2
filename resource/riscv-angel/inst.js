
// fills upper 32 bits with sign bit of lower 32 bits
function signExtLT32_64(quantity) {
    return new Long(quantity|0, quantity >> 31);
}

function runInstruction(raw) {
    switch(raw & 0x7F) { // major instruction switch copied
    
        // J-TYPE (JAL) - opcode: 0b1101111
        case 0x6F:
            RISCV.gen_reg[((raw >>> 7) & 0x1F)] = signExtLT32_64(RISCV.pc + 4);
            RISCV.pc = (RISCV.pc|0) + (((raw >> 20) & 0xFFF007FE) | ((raw >>> 9) & 0x00000800) | (raw & 0x000FF000));
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

        // R-TYPES (continued): System instructions
        case 0x73:
            var superfunct = ((raw >>> 12) & 0x7) | ((raw >>> 20) & 0x1F) << 3 | ((raw >>> 25) & 0x7F) << 8;
            switch(superfunct) {

                // SCALL
                case 0x0:
                    RISCV.excpTrigg = new RISCVTrap("System Call");
                    return;
                    break;

                // SBREAK
                case 0x8:
                    RISCV.excpTrigg = new RISCVTrap("Breakpoint");
                    return;
                    RISCV.pc += 4;
                    break;

                // SRET
                case 0x4000:
                    // [todo] - need to check for supervisor?
                    // first, confirm that we're in supervisor mode
//                    if ((RISCV.priv_reg[PCR["CSR_STATUS"]["num"]] & SR["SR_S"]) == 0) {
//                        throw new RISCVTrap("Privileged Instruction");
//                    }
                    // do eret stuff here
                    var oldsr = RISCV.priv_reg[PCR["CSR_STATUS"]["num"]];
                    // set SR[S] = SR[PS], don't touch SR[PS]
                    if ((oldsr & SR["SR_PS"]) != 0) {
                        // PS is set
                        oldsr = oldsr | SR["SR_S"];
                    } else {
                        oldsr = oldsr & (~SR["SR_S"]);
                    }
                    // set EI
                    if ((oldsr & SR["SR_PEI"]) != 0) {
                        oldsr = oldsr | SR["SR_EI"];
                    } else {
                        oldsr = oldsr & (~SR["SR_EI"]);
                    }
        
                    // store updated SR back:
                    RISCV.priv_reg[PCR["CSR_STATUS"]["num"]] = oldsr;



                    // set pc to value stored in EPC
                    RISCV.pc = RISCV.priv_reg[PCR["CSR_EPC"]["num"]].getLowBits();
//                    RISCV.pc += 4;
                    break;

                // RDCYCLE
                case 0x6002:
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(RISCV.priv_reg[PCR["CSR_CYCLE"]["num"]], 0x0);
                    RISCV.pc += 4;
                    break;

                // RDTIME
                case 0x600A:
                    // places #ms since cpu boot in rd. against spec 
                    // but the best we can reasonably do with js
                    var nowtime = new Date();
                    nowtime = nowtime.getTime();
                    // need to be careful here: the subtraction needs to be
                    // done as a float to cut down to reasonable number of
                    // bits, then or with zero to get close by int value
                    var result = nowtime - RISCV.priv_reg[PCR["CSR_TIME"]["num"]].toNumber();
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = Long.fromNumber(result);
                    RISCV.pc += 4;
                    break;

                // RDINSTRET
                case 0x6012:
                    // for our purposes, this is the same as RDCYCLE:
                    RISCV.gen_reg[((raw >>> 7) & 0x1F)] = RISCV.priv_reg[PCR["CSR_INSTRET"]["num"]];
                    RISCV.pc += 4;
                    break;

                default:
                    // if none of the above are triggered, try handling as CSR inst
                    var funct3 = ((raw >>> 12) & 0x7);
                    //var rd = RISCV.gen_reg[((raw >>> 7) & 0x1F)];
                    var rs1 = RISCV.gen_reg[((raw >>> 15) & 0x1F)];
                    switch(funct3) {

                        // [todo] - currently does not perform permission check

                        // CSRRW
                        case 0x1:
                            var timm = ((raw >>> 20));
                            if (timm == 0x3 || timm == 0x2 || timm == 0x1) {
                                RISCV.excpTrigg =  new RISCVTrap("Floating-Point Disabled");
                                return;
                            }
                            var temp = RISCV.priv_reg[((raw >>> 20))];
                            if (typeof temp === "number") {
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(temp, 0x0);
                                temp = rs1.getLowBitsUnsigned();
                            } else {
                                //temp is a long
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = temp;
                                temp = rs1;
                            }
                            RISCV.set_pcr(((raw >>> 20)), temp);
                            if (((raw >>> 20)) == PCR["CSR_FATC"]["num"]) {
                                TLB = new Uint32Array(TLBSIZE);
                                ITLB = new Uint32Array(ITLBSIZE);
                                ITLBstuff = new Uint32Array(ITLBSIZE);

 //                               console.log("flushing TLB from CSRRW");
 //                               console.log("Current ASID is " + stringIntHex(RISCV.priv_reg[PCR["CSR_ASID"]["num"]]));
                            }
                            RISCV.pc += 4;
                            break;

                        // CSRRS
                        case 0x2:

                            var timm = ((raw >>> 20));
                            if ((timm == 0x3 || timm == 0x2 || timm == 0x1) && (((raw >>> 15) & 0x1F) == 0x0)) {
                                RISCV.excpTrigg = new RISCVTrap("Floating-Point Disabled");
                                return;
                            }
                            var temp = RISCV.priv_reg[((raw >>> 20))];
                            if (typeof temp === "number") {
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(temp, 0x0);
                                temp = temp | rs1.getLowBitsUnsigned();
                            } else {
                                //temp is a long
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = temp;
                                temp = temp.or(rs1);
                            }
                            RISCV.set_pcr(((raw >>> 20)), temp);
                            RISCV.pc += 4;
                            break;

                        // CSRRC
                        case 0x3:
                            var temp = RISCV.priv_reg[((raw >>> 20))];
                            if (typeof temp === "number") {
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(temp, 0x0);
                                temp = temp & ~(rs1.getLowBitsUnsigned());
                            } else {
                                //temp is a long
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = temp;
                                temp = temp.and(rs1.not());
                            }
                            RISCV.set_pcr(((raw >>> 20)), temp);
                            RISCV.pc += 4;
                            break;

                        // CSRRWI
                        case 0x5:
                            var temp = RISCV.priv_reg[((raw >>> 20))];
                            var tempbak = temp;
                            if (typeof temp === "number") {
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(temp, 0x0);
                                temp = ((raw >>> 15) & 0x1F) & 0x0000001F;
                            } else {
                                //temp is a long
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = temp;
                                temp = new Long(((raw >>> 15) & 0x1F) & 0x0000001F, 0x0);
                            }
                            RISCV.set_pcr(((raw >>> 20)), temp);
                            if (((raw >>> 20)) == PCR["CSR_FATC"]["num"]) {
                                TLB = new Uint32Array(TLBSIZE);
                                ITLB = new Uint32Array(ITLBSIZE);
                                ITLBstuff = new Uint32Array(ITLBSIZE);

//                                console.log("flushing TLB from CSRRWI");
//                                console.log("Current ASID is " + stringIntHex(RISCV.priv_reg[PCR["CSR_ASID"]["num"]]));
//                                console.log("Value written to FATC is " + stringIntHex(tempbak));
                            }
                            RISCV.pc += 4;
                            break;

                        // CSRRSI
                        case 0x6:
                            var temp = RISCV.priv_reg[((raw >>> 20))];
                            if (typeof temp === "number") {
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(temp, 0x0);
                                temp = temp | (((raw >>> 15) & 0x1F) & 0x0000001F);
                            } else {
                                //temp is a long
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = temp;
                                temp = temp.or(new Long(((raw >>> 15) & 0x1F) & 0x0000001F, 0x0));
                            }
                            RISCV.set_pcr(((raw >>> 20)), temp);
                            RISCV.pc += 4;
                            break;

                        // CSRRCI
                        case 0x7:
                            var temp = RISCV.priv_reg[((raw >>> 20))];
                            if (typeof temp === "number") {
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = new Long(temp, 0x0);
                                temp = temp & ~(((raw >>> 15) & 0x1F) & 0x0000001F);
                            } else {
                                //temp is a long
                                RISCV.gen_reg[((raw >>> 7) & 0x1F)] = temp;
                                temp = temp.and(new Long(((raw >>> 15) & 0x1F) & 0x0000001F, 0x0).not());
                            }
                            RISCV.set_pcr(((raw >>> 20)), temp);
                            RISCV.pc += 4;
                            break;


                        default:
                            throw new RISCVTrap("Illegal Instruction");
                            break;

                    }
                    break;

            }
            break;



    }

    // finally, increment cycle counter, instret counter, count register:
//    RISCV.priv_reg[PCR["CSR_INSTRET"]["num"]] = RISCV.priv_reg[PCR["CSR_INSTRET"]["num"]].add(Long.ONE);
    RISCV.priv_reg[PCR["CSR_CYCLE"]["num"]] += 1;
    RISCV.priv_reg[PCR["CSR_COUNT"]["num"]] += 1;
}
