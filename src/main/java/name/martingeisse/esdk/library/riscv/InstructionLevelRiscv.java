package name.martingeisse.esdk.library.riscv;

/**
 * Note: Interrupts are not supported for now.
 */
public abstract class InstructionLevelRiscv {

	private final int[] registers = new int[32];
	private int pcInWords;

	/**
	 * Resets the CPU.
	 */
	public void reset() {
		pcInWords = 0;
	}

	/**
	 * Executes a single instruction.
	 */
	public void step() {
		int instruction = fetchInstruction(pcInWords);
		switch (instruction) {

		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// PC
	// ----------------------------------------------------------------------------------------------------------------

	public final int getPcInWords() {
		return pcInWords;
	}

	public final void setPcInWords(int pcInWords) {
		this.pcInWords = pcInWords;
	}

	public final int getPc() {
		return pcInWords << 2;
	}

	public final void setPc(int pc) {
		if ((pc & 3) != 0) {
			onException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
		} else {
			this.pcInWords = pc >> 2;
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// general-purpose registers
	// ----------------------------------------------------------------------------------------------------------------

	public final int getRegister(int index) {
		checkRegisterIndex(index);
		return registers[index];
	}

	public final void setRegister(int index, int value) {
		checkRegisterIndex(index);
		if (index != 0) {
			registers[index] = value;
		}
	}

	private void checkRegisterIndex(int index) {
		if (index < 0 || index >= 32) {
			throw new RuntimeException("invalid register index: " + index);
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// abstract behavior
	// ----------------------------------------------------------------------------------------------------------------

	protected abstract int fetchInstruction(int wordAddress);
	protected abstract int read(int wordAddress);
	protected abstract void write(int wordAddress, int data, int mask);

	protected void onException(ExceptionType type) {
		throw new RuntimeException("RISC-V cpu exception: " + type);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// helper classes
	// ----------------------------------------------------------------------------------------------------------------

	public enum ExceptionType {
		INSTRUCTION_ADDRESS_MISALIGNED
	}

}
