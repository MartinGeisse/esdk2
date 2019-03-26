package name.martingeisse.esdk.library.riscv;

import org.junit.Assert;
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

	@Test
	public void testAddi() {
		NoBusInstructionLevelRiscv cpu = new NoBusInstructionLevelRiscv(
			InstructionEncoder.addi(1, 0, 0),
			InstructionEncoder.addi(1, 1, 0),
			InstructionEncoder.addi(1, 1, 1),
			InstructionEncoder.addi(1, 1, 2),
			InstructionEncoder.addi(1, 1, 3)
		);
		cpu.step();
		Assert.assertEquals(0, cpu.getRegister(1));
		cpu.step();
		Assert.assertEquals(0, cpu.getRegister(1));
		cpu.step();
		Assert.assertEquals(1, cpu.getRegister(1));
		cpu.step();
		Assert.assertEquals(3, cpu.getRegister(1));
		cpu.step();
		Assert.assertEquals(6, cpu.getRegister(1));
		cpu.assertTrace(0, 1, 2, 3, 4);
	}

}
