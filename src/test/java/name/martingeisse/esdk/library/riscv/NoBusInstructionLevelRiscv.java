package name.martingeisse.esdk.library.riscv;

import org.junit.Assert;

/**
 *
 */
public class NoBusInstructionLevelRiscv extends InstructionLevelRiscv {

	private final int[] instructions;
	private final Trace trace;

	public NoBusInstructionLevelRiscv(int... instructions) {
		this.instructions = instructions;
		this.trace = new Trace();
	}

	@Override
	public int fetchInstruction(int wordAddress) {
		Assert.assertTrue("instruction address is out of bounds", wordAddress >= 0 && wordAddress < instructions.length);
		trace.append(wordAddress);
		return instructions[wordAddress];
	}

	@Override
	public int read(int wordAddress) {
		Assert.fail("test code tries to read from memory");
		return 0;
	}

	@Override
	public void write(int wordAddress, int data, int byteMask) {
		Assert.fail("test code tries to write to memory");
	}

	public Trace getTrace() {
		return trace;
	}

	public void printTrace() {
		System.out.println("trace: " + trace);
	}

	public void assertTrace(int... expectedElements) {
		trace.assertEquals(expectedElements);
	}

}
