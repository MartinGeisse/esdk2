package name.martingeisse.esdk.structural.midlevel;

/**
 * This base class contains the CPU state and methods that resemble the CPU instructions.
 *
 * This class is abstract. A concrete subclass must implement a memory map.
 *
 * This class does not contain a PC register since that is still represented by Java's "PC". Branch instructions just
 * return whether they would branch, and a jump instruction does not exist at this level.
 *
 * NOTE YET IMPLEMENTED: bit, biti, cmp, cmpi -- because they *need* separate N and Z flag registers. I imagined that
 * N and Z could be derived from the contents of the a register, but then there can't be instructions like these that
 * "set" the flags without changing the a register. Let's first see if I need them, and if so, add flag registers.
 *
 * NOT YET IMPLEMENTED: shift instructions, because I don't know if I need them. Let's see that first.
 *
 * NOT YET IMPLEMENTED: branch on negative flag, because I'm not sure if I need that. I don't have signed arithmetic,
 * and if a subtraction goes below zero, I can detect that by inspecting the carry flag.
 *
 * ---
 *
 * TODO: With "absolute" versions of the instructions, the a register is heavily underused. Originally I wanted to
 * have "immediate" and "indirect" versions. The indirect variants are definitely needed for computed addresses.
 * The absolute variants probably reduce code size a lot. For now, I'll add both and have a look how much the
 * absolute variants get used. In the CPu architecture, this places the memory "before" the ALU to load, then compute.
 * We don't have memory accesses after computation. (compare to MIPS, where no computation can occur after a memory
 * access, but address computation happens before memory access, so the memory sits behind the ALU).
 *
 * Instruction variants: -i immediate, -a absolute, -n indirect. The -a/-n bit muxes the memory address (immediate
 * value or a register), and the -i muxes the result value (memory or immediate value). If the instruction space is
 * empty enough, this allows to use the a register value directly as the second operand (this "if" means: If we don't
 * need those bit combinations to mean other instructions because we are out of instruction bits). If this works, I'd
 * rename the register to X and Y since their meaning is much more symmetrical (though not completely: only Y can be
 * used for addressing -- not Y, since it is more "natural" to make the same register the left operand that is used
 * to store the result).
 *
 * Operations: add, addc, sub, subc, and, or, xor, load.
 *
 * This makes 3 bits for the operation and 2 bits for the source.
 *
 * This leaves 3 bits for the primary opcode:
 * 000 - compute (includes add/addc/sub/subc/and/or/xor/load, -a/-i/-n), also ld, ldi, tad
 * 001 - store
 * 010 - tda, la, lai
 * ??? - brz, bnz, brc, bnc, jmp
 * --> use 2-to-4 decoder for the upper two address bits; next 2 bits mux the address (imm/a) and the right operand
 *  (imm/mem)
 *
 * The address mux allows "store absolute" and "store indirect".
 *
 * Note that the 4-bit operation has to be decoded to 5 ALU function signals plus one carry suppress signal plus one
 * constant-carry signal. We might end up not needing some of them, possibly a single adder plus mux is enough.
 *
 * TODO: having all operation set the carry, including txy and tyx, might interfere with transferring the frame buffer.
 * I might have to change this so only add/sub set the carry, i.e. the same operations that use a carry inside the ALU.
 */
public abstract class AbstractCpuProgramFragments {

//region state

    // bank
    private int b;

    // left operand
    private int x;

    // right operand
    private int y;

    // carry
    private boolean carry;

//endregion
//region abstraction

    protected abstract int read(int address);
    protected abstract void write(int address, int value);

//endregion
//region accessors


    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isCarry() {
        return carry;
    }

    public void setCarry(boolean carry) {
        this.carry = carry;
    }

//endregion
//region computation instructions

    public enum AddressingMode {

        // use immediate byte directly
        IMMEDIATE,

        // use immediate byte as address
        ABSOLUTE,

        // use Y register directly
        Y,

        // use Y register as address
        INDIRECT

    }

    private int getRightOperand(AddressingMode addressingMode, int immediateValue) {
        immediateValue = immediateValue & 0xff;
        switch (addressingMode) {

            case IMMEDIATE:
                return immediateValue;

            case ABSOLUTE:
                return read(immediateValue) & 0xff;

            case Y:
                return y;

            case INDIRECT:
                return read(y) & 0xff;

            default:
                throw new RuntimeException();

        }
    }

    public enum Operation {
        ADD, ADDC, SUB, SUBC, AND, OR, XOR, XNOR, LEFT, RIGHT
    }

    // note: affects the carry flag!
    private int performOperation(Operation operation, int left, int right) {
        boolean oldCarry = carry;
        carry = false;
        switch (operation) {

            case ADD:
                carry = (left + right) > 0xff;
                return (left + right) & 0xff;

            case ADDC:
                carry = (left + right + (oldCarry ? 1 : 0)) > 0xff;
                return (left + right + (oldCarry ? 1 : 0)) & 0xff;

            case SUB: // borrow = !carry
                carry = left >= right;
                return (left - right) & 0xff;

            case SUBC: // borrow = !carry
                carry = left >= (right + (oldCarry ? 0 : 1));
                return (left - (right + (oldCarry ? 0 : 1))) & 0xff;

            case AND:
                return left & right;

            case OR:
                return left | right;

            case XOR:
                return left ^ right;

            case XNOR:
                return ~(left ^ right);

            case LEFT:
                return left;

            case RIGHT:
                return right;

            default:
                throw new RuntimeException();

        }
    }

    public enum Destination {
        X, Y
    }

    private void setResult(Destination destination, int result) {
        result = result & 0xff;
        switch (destination) {

            case X:
                x = result;
                break;

            case Y:
                y = result;
                break;

            default:
                throw new RuntimeException();

        }
    }

    public void operation(Operation operation, AddressingMode addressingMode, Destination destination, int immediateValue) {
        setResult(destination, performOperation(operation, x, getRightOperand(addressingMode, immediateValue)));
    }

//endregion
//region store instructions

    public void storeAbsolute(int immediateValue) {
        write(immediateValue & 0xff, x);
    }

    public void storeIndirect() {
        write(y, x);
    }

//endregion
//region branch/jump instructions

    public enum Condition {
        CARRY, NOT_CARRY, ZERO, NOT_ZERO, ALWAYS
    }

//endregion
//region instructions

    public boolean branch(Condition condition) {
        switch (condition) {

            case CARRY:
                return carry;

            case NOT_CARRY:
                return !carry;

            case ZERO:
                return (x == 0);

            case NOT_ZERO:
                return (x != 0);

            case ALWAYS:
                return true;

            default:
                throw new RuntimeException();

        }
    }

//endregion

}
