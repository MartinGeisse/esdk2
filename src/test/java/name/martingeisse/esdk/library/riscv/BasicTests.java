package name.martingeisse.esdk.library.riscv;

import org.junit.Test;

/**
 *
 */
public class BasicTests {

	// canonical NOP: OP-IMM/ADDI r0 = r0 + 0
	private static final int NOP = 0x13;

	@Test
	public void testNops() {
		NoBusInstructionLevelRiscv cpu = new NoBusInstructionLevelRiscv(NOP, NOP, NOP);
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.assertTrace(0, 1, 2);
	}

}
