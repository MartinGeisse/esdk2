
// exception class (general)
function RISCVError(message) {
    this.name = "RISCVError";
    this.message = (message || "");
    this.e_type = "RISCVError";
    console.log(stringIntHex(RISCV.pc));
}
RISCVError.prototype = Error.prototype;

// exception class (traps); memaddr is for load/store misaligned/access faults
function RISCVTrap(message, memaddr) {
    this.name = "RISCVTrap";
    this.message = (message || "");
    this.memaddr = memaddr;
    this.exceptionCode = TRAPS[this.message][0];
    this.interruptBit = TRAPS[this.message][1]; // by def
    this.e_type = "RISCVTrap";
}
RISCVTrap.prototype = Error.prototype;




// unsigned comparison of longs
// return true if long1 < long2
function long_less_than_unsigned(long1, long2) {
    var long1up = signed_to_unsigned(long1.getHighBits());
    var long2up = signed_to_unsigned(long2.getHighBits());

    if (long1up < long2up) {
        return true;
    } else if (long1up > long2up) {
        return false;
    } else {
        var long1down = signed_to_unsigned(long1.getLowBits());
        var long2down = signed_to_unsigned(long2.getLowBits());
        return (long1down < long2down);        
    }
}

/* this 'converts' a signed number in javascript to an 
 * unsigned number. 
 * by default, javascript will take the 'value' stored in a number
 * and compare it (the definition of value is rather fluid). instead, 
 * we want to convert signed numbers by 
 * stripping out the MSB and adding 2^31 if the MSB is set (ie if the 
 * value is negative). Such a number can be represented by JS built in 
 * Numbers (64 bit float), and then comparing these values without performing
 * any bitwise ops on them will effectively do an unsigned comparison 
 */
function signed_to_unsigned(inputNum) {
    if ((inputNum & 0x80000000) == 0) {
        return inputNum;
    } else {
        return (inputNum & 0x7FFFFFFF) + Math.pow(2, 31);
    }
}
