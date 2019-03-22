// CPU class. Contains regfile, memory, and special registers
// memamt is memory size in Mebibytes, default to 32
function CPU(memamt) {
    memamt = typeof memamt !== 'undefined' ? memamt : 10;

    this.memamount = memamt; // for use by the kernel
    
    memamt *= 1048576 // convert to Bytes
    this.memory = new Uint32Array(memamt >> 2);

    this.excpTrigg = undefined

    // PC, defaults to 0x2000 according to the ISA, documented in processor.cc
    // Even in RV64, this must remain as a Number (not a Long) because of Array indexing requirements.
    this.pc = 0x2000;

    // general-purpose registers, gen_reg[0] is x0, etc.
    this.gen_reg = [];
    
    for (var i = 0; i < 32; i++) {
        this.gen_reg[i] = new Long(0x0, 0x0);
    }

    // privileged control registers
    this.priv_reg = new Array(3075);
    
    for (var key in PCR) {
        if (PCR.hasOwnProperty(key)) {
            if (PCR[key]["width"] == 32) {
                this.priv_reg[PCR[key]["num"]] = 0x0;
            } else {
                // 64 bit
                this.priv_reg[PCR[key]["num"]] = new Long(0x0, 0x0);
            }
        }
    }

    // init status register. At RESET, processor starts with ET=0, S=1, VM=0, EI=0, PS=0, then force implementation defined presets.
    this.priv_reg[PCR["CSR_STATUS"]["num"]] = SR["SR_S"];

    this.instcount = 0x1; // special counter for MIPS measurement, start at one
                          // to avoid incorrect first result

    // record cpu boot time (in ms since jan 1, 1970) for rdtime instruction
    // for better measurement, this should be reset right before first instruction
    // is exec'd
    var start = new Date();
    this.priv_reg[PCR["CSR_TIME"]["num"]] = Long.fromNumber(start.getTime());

    function reset_wall_clock() {
        // this should be called once, right before exec of first instruction
        var start = new Date();
        this.priv_reg[PCR["CSR_TIME"]["num"]] = Long.fromNumber(start.getTime());
    }

    // unlike word, half, byte, the val arg here is a Long
    function store_double_to_mem(addr, val) {...}
    function store_word_to_mem(addr, val) {...}
    function store_half_to_mem(addr, val) {...}
    function store_byte_to_mem(addr, val) {...}
    function load_double_from_mem(addr) {...}
    function load_double_from_mem_raw(addr) {...}
    function load_word_from_mem(addr) {...}
    function load_half_from_mem(addr) {...}
    function load_byte_from_mem(addr) {...}
    function load_inst_from_mem(addr) {...}


    // set indicated PCR - need to make sure to prevent changes to hardwired vals
    function set_pcr(num, val) {
        switch(num) {
            case PCR["CSR_STATUS"]["num"]:
                // assuming 32 bit status reg
                // "hardwired" values that need to be forced every time status reg is modified
                // force EF to zero here (no FP insts); force U64 to 1 here; force S64 to 1 here
                this.priv_reg[num] = (val & (~SR["SR_EF"])) | SR["SR_U64"] | SR["SR_S64"];
                break;

            // need to fill in all cases here (i.e. when implementing interrupts)
            case PCR["CSR_TOHOST"]["num"]:
                if (this.priv_reg[num].isZero()) {
                    this.priv_reg[num] = val;
                }
                break;

            default:
                this.priv_reg[num] = val;
                break; 

        }
    }

}
