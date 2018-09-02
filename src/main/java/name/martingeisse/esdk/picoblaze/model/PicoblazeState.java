/**
 * Copyright (c) 2015 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.picoblaze.model;

/**
 * An abstraction level-neutral representation of the internal state of a PicoBlaze
 * instance. This state model can be used both for simulating a PB program within
 * an instruction-level simulator and simulating the PB in a hardware system at
 * register transfer level.
 * <p>
 * Note that the instruction store is *NOT* part of this model.
 */
public abstract class PicoblazeState {

	// ISA-visible state
	private final byte[] registers;
	private boolean zero;
	private boolean carry;
	private final byte[] ram;
	private final int[] returnStack;
	private int returnStackPointer;
	private boolean interruptEnable;
	private boolean preservedZero;
	private boolean preservedCarry;

	// instruction execution state
	private int instruction;

	/**
	 * Constructor.
	 */
	public PicoblazeState() {
		this.registers = new byte[16];
		this.ram = new byte[64];
		this.returnStack = new int[32];
		reset();
	}

	/**
	 * Resets the PB state. Specifically, this sets the PC to 0 and clears the
	 * INTERRUPT ENABLE, CARRY and ZERO flags. Other state is not affected.
	 */
	public void reset() {
		setPc(0);
		setInterruptEnable(false);
		setZero(false);
		setCarry(false);
		setInstruction(0x01000); // LOAD s0, s0
	}

//region subclass behavior

	protected abstract int handleInput(int address);

	protected abstract void handleOutput(int address, int value);

//endregion

//region accessors

	/**
	 * Getter method for the pc.
	 *
	 * @return the pc
	 */
	public int getPc() {
		return (returnStack[returnStackPointer] & 1023);
	}

	/**
	 * Setter method for the pc.
	 *
	 * @param pc the pc to set
	 */
	public void setPc(final int pc) {
		this.returnStack[returnStackPointer] = (pc & 1023);
	}

	/**
	 * Getter method for the zero.
	 *
	 * @return the zero
	 */
	public boolean isZero() {
		return zero;
	}

	/**
	 * Setter method for the zero.
	 *
	 * @param zero the zero to set
	 */
	public void setZero(final boolean zero) {
		this.zero = zero;
	}

	/**
	 * Getter method for the carry.
	 *
	 * @return the carry
	 */
	public boolean isCarry() {
		return carry;
	}

	/**
	 * Setter method for the carry.
	 *
	 * @param carry the carry to set
	 */
	public void setCarry(final boolean carry) {
		this.carry = carry;
	}

	/**
	 * Getter method for the returnStackPointer.
	 *
	 * @return the returnStackPointer
	 */
	public int getReturnStackPointer() {
		return returnStackPointer;
	}

	/**
	 * Setter method for the returnStackPointer.
	 *
	 * @param returnStackPointer the returnStackPointer to set
	 */
	public void setReturnStackPointer(final int returnStackPointer) {
		this.returnStackPointer = (returnStackPointer & 31);
	}

	/**
	 * Getter method for the registers.
	 *
	 * @return the registers
	 */
	public byte[] getRegisters() {
		return registers;
	}

	/**
	 * Getter method for the ram.
	 *
	 * @return the ram
	 */
	public byte[] getRam() {
		return ram;
	}

	/**
	 * Getter method for the returnStack.
	 *
	 * @return the returnStack
	 */
	public int[] getReturnStack() {
		return returnStack;
	}

	/**
	 * Getter method for the interruptEnable.
	 *
	 * @return the interruptEnable
	 */
	public boolean isInterruptEnable() {
		return interruptEnable;
	}

	/**
	 * Setter method for the interruptEnable.
	 *
	 * @param interruptEnable the interruptEnable to set
	 */
	public void setInterruptEnable(final boolean interruptEnable) {
		this.interruptEnable = interruptEnable;
	}

	/**
	 * Getter method for the preservedZero.
	 *
	 * @return the preservedZero
	 */
	public boolean isPreservedZero() {
		return preservedZero;
	}

	/**
	 * Setter method for the preservedZero.
	 *
	 * @param preservedZero the preservedZero to set
	 */
	public void setPreservedZero(final boolean preservedZero) {
		this.preservedZero = preservedZero;
	}

	/**
	 * Getter method for the preservedCarry.
	 *
	 * @return the preservedCarry
	 */
	public boolean isPreservedCarry() {
		return preservedCarry;
	}

	/**
	 * Setter method for the preservedCarry.
	 *
	 * @param preservedCarry the preservedCarry to set
	 */
	public void setPreservedCarry(final boolean preservedCarry) {
		this.preservedCarry = preservedCarry;
	}

	/**
	 * Returns the value of the specified register
	 *
	 * @param registerNumber the register number (only the lowest four bits are considered)
	 * @return the value of the register, in the range 0-255
	 */
	public int getRegisterValue(final int registerNumber) {
		return (registers[registerNumber & 15] & 0xff);
	}

	/**
	 * Sets the value of the specified register
	 *
	 * @param registerNumber the register number (only the lowest four bits are considered)
	 * @param value          the value to set (only the lowest eight bits are considered)
	 */
	public void setRegisterValue(final int registerNumber, final int value) {
		registers[registerNumber & 15] = (byte) value;
	}

	/**
	 * Returns the value of the specified RAM cell
	 *
	 * @param address the RAM address (only the lowest six bits are considered)
	 * @return the value of the RAM cell, in the range 0-255
	 */
	public int getRamValue(final int address) {
		return (ram[address & 63] & 0xff);
	}

	/**
	 * Sets the value of the RAM cell.
	 *
	 * @param address the RAM address (only the lowest six bits are considered)
	 * @param value   the value to set (only the lowest eight bits are considered)
	 */
	public void setRamValue(final int address, final int value) {
		ram[address & 63] = (byte) value;
	}

	/**
	 * Returns the byte value (0-255) of either a register or the specified value.
	 *
	 * @param x         the register number or immediate value
	 * @param immediate whether the value is immediate
	 * @return the byte value
	 */
	private int getRegisterOrImmediate(final int x, final boolean immediate) {
		return (immediate ? (x & 0xff) : getRegisterValue(x));
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	//endregion

//region instruction decoding

	/**
	 * Returns the primary opcode, which is the highest 5 bits of the instruction.
	 */
	public int getPrimaryOpcode() {
		return ((instruction >> 13) & 31);
	}

	/**
	 * Gets the left operand value in the range 0..255
	 */
	public int getLeftOperand() {
		return getRegisterValue((instruction >> 8) & 15);
	}

	/**
	 * Sets the left operand value. Only the low 8 bits are stored.
	 */
	public void setLeftOperand(int value) {
		setRegisterValue((instruction >> 8) & 15, value);
	}

	/**
	 * Gets the right operand value in the range 0..255
	 */
	public int getRightOperand() {
		if (((instruction >> 12) & 1) == 0) {
			// immediate
			return (instruction & 255);
		} else {
			// register
			return getRegisterValue((instruction >> 4) & 15);
		}
	}

	/**
	 * Decodes the jump/return condition from the instruction and tests it against the current flag state.
	 */
	public boolean testCondition() {
		return PicoblazeJumpCondition.fromEncodedInstruction(instruction).test(zero, carry);
	}

//endregion

//region RTL signal support

	public int getPortOutputData() {
		return getLeftOperand();
	}

	public int getPortAddress() {
		return getRightOperand();
	}

	public boolean isInputInstruction() {
		return getPrimaryOpcode() == 2;
	}

	public boolean isOutputInstruction() {
		return getPrimaryOpcode() == 22;
	}

//endregion

//region instruction execution

	// ----------------------------------------------------------------------------------------------------------------
	// instruction execution
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * @param shiftInValue true to shift in a 1 bit, false to shift in a 0 bit
	 */
	private void performShiftLeft(final boolean shiftInValue) {
		int x = getLeftOperand();
		setCarry((x & 128) != 0);
		x = ((x << 1) | (shiftInValue ? 1 : 0));
		setLeftOperand(x);
		setZero((x & 0xff) == 0);
	}

	/**
	 * @param shiftInValue true to shift in a 1 bit, false to shift in a 0 bit
	 */
	private void performShiftRight(final boolean shiftInValue) {
		int x = getLeftOperand();
		setCarry((x & 1) != 0);
		x = ((x >> 1) | (shiftInValue ? 128 : 0));
		setLeftOperand(x);
		setZero((x & 0xff) == 0);
	}

	/**
	 * This method does not correspond to an instruction, but to an asserted interrupt signal.
	 */
	public void performInterrupt() {
		setReturnStackPointer(getReturnStackPointer() + 1);
		setPc(-1);
		setPreservedZero(isZero());
		setPreservedCarry(isCarry());
		setInterruptEnable(false);
	}

	/**
	 * Performs a shift or rotate instruction. This method is used for all types of shift and rotate, left and right.
	 */
	private void performShiftOrRotateInstruction() {
		final int subOpcode = instruction & 15;
		switch (subOpcode) {

			case 0:
				performShiftLeft(carry);
				break;

			case 2:
				performShiftLeft((getLeftOperand() & 128) != 0);
				break;

			case 4:
				performShiftLeft((getLeftOperand() & 1) != 0);
				break;

			case 6:
				performShiftLeft(false);
				break;

			case 7:
				performShiftLeft(true);
				break;

			case 8:
				performShiftRight(carry);
				break;

			case 10:
				performShiftRight((getLeftOperand() & 128) != 0);
				break;

			case 12:
				performShiftRight((getLeftOperand() & 1) != 0);
				break;

			case 14:
				performShiftRight(false);
				break;

			case 15:
				performShiftRight(true);
				break;

			// undefined shift sub-opcodes: 1, 3, 5, 9, 11, 13
			default:
				throw new UndefinedInstructionCodeException("Unknown shift instruction sub-opcode: " + subOpcode);

		}
	}

	public void performFirstCycle() {
		setPc(getPc() + 1);
		switch (getPrimaryOpcode()) {

			// RETURN*
			case 21:
				if (testCondition()) {
					setReturnStackPointer(getReturnStackPointer() - 1);
				}
				break;

			// CALL*
			// Implementation note: We store the address of the instruction *after* the call on the stack, not the
			// address of the call as described in the docs. Since the stack cannot be accessed, this shouldn't make
			// a difference.
			case 24:
				if (testCondition()) {
					setReturnStackPointer(getReturnStackPointer() + 1);
					setPc(instruction & 1023);
				}
				break;

			// JUMP*
			case 26:
				if (testCondition()) {
					setPc(instruction & 1023);
				}
				break;

			// RETURNI*
			case 28:
				setReturnStackPointer(getReturnStackPointer() - 1);
				setZero(isPreservedZero());
				setCarry(isPreservedCarry());
				setInterruptEnable((instruction & 1) != 0);
				break;

		}
	}

	public void performSecondCycle() {
		switch (getPrimaryOpcode()) {

			// LOAD
			case 0:
				setLeftOperand(getRightOperand());
				break;

			// INPUT
			case 2:
				setLeftOperand(handleInput(getRightOperand()));
				break;

			// FETCH
			case 3:
				setLeftOperand(getRamValue(getRightOperand()));
				break;

			// AND
			case 5: {
				final int result = getLeftOperand() & getRightOperand();
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(false);
				break;
			}

			// OR
			case 6: {
				final int result = getLeftOperand() | getRightOperand();
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(false);
				break;
			}

			// XOR
			case 7: {
				final int result = getLeftOperand() ^ getRightOperand();
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(false);
				break;
			}

			// TEST
			case 9: {
				int temp = getLeftOperand() & getRightOperand();
				setZero(temp == 0);
				temp ^= (temp >> 4);
				temp ^= (temp >> 2);
				temp ^= (temp >> 1);
				setCarry((temp & 1) != 0);
				break;
			}

			// COMPARE
			case 10: {
				final int temp = getLeftOperand() - getRightOperand();
				setZero(temp == 0);
				setCarry(temp < 0);
				break;
			}

			// ADD
			case 12: {
				final int result = getLeftOperand() + getRightOperand();
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(result > 255);
				break;
			}

			// ADDCY
			case 13: {
				final int result = getLeftOperand() + getRightOperand() + (carry ? 1 : 0);
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(result > 255);
				break;
			}

			// SUB
			case 14: {
				final int result = getLeftOperand() - getRightOperand();
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(result < 0);
				break;
			}

			// SUBCY
			case 15: {
				final int result = getLeftOperand() - getRightOperand() - (carry ? 1 : 0);
				setLeftOperand(result);
				setZero((result & 0xff) == 0);
				setCarry(result < 0);
				break;
			}

			// all shift and rotate instructions
			case 16:
				performShiftOrRotateInstruction();
				break;

			// RETURN* -- already done in first cycle
			case 21:
				break;

			// OUTPUT
			case 22:
				handleOutput(getRightOperand(), getLeftOperand());
				break;

			// STORE
			case 23:
				setRamValue(getRightOperand(), getLeftOperand());
				break;

			// CALL* -- already done in first cycle
			case 24:
				break;

			// JUMP* -- already done in first cycle
			case 26:
				break;

			// RETURNI* -- already done in first cycle
			case 28:
				break;

			// ENABLE INTERRUPT, DISABLE INTERRUPT
			case 30:
				setInterruptEnable((instruction & 1) != 0);
				break;

			// undefined primary opcodes: 1, 4, 8, 11, 17-20, 25, 27, 29, 31
			default:
				throw new UndefinedInstructionCodeException("unknown primary opcode (highest five bits): " + getPrimaryOpcode());

		}
	}

//endregion

}
