package name.martingeisse.esdk.library.riscv;

/**
 *
 */
public abstract class InstructionLevelRiscv {

	private final int[] registers = new int[32];
	private int pc;

	/**
	 * Resets the CPU.
	 */
	public void reset() {
		pc = 0;
	}

	/**
	 * Executes a single instruction.
	 */
	public void step() {
		int instruction = fetchInstruction(pc);
		switch (instruction) {

		}
	}

	public void interrupt() {

	}

	public int getPc() {
		return pc;
	}

	public int[] getRegisters() {
		return registers;
	}

	// TODO word or byte addresses?
	protected abstract int fetchInstruction(int pc);
	protected abstract int read(int address);
	protected abstract void write(int address, int data);

}
