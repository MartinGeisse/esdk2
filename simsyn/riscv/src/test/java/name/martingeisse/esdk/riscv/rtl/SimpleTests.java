package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.util.ClockStepper;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class SimpleTests {

	private final Design design;
	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final Multicycle.Implementation cpu;
	private final ClockStepper clockStepper;
	private final InstructionStepper stepper;

	public SimpleTests() {
		design = new Design();
		realm = new RtlRealm(design);
		clock = new RtlClockNetwork(realm);
		cpu = new Multicycle.Implementation(realm, clock);
		cpu.setReset(new RtlBitConstant(realm, false));
		cpu.setInterrupt(new RtlBitConstant(realm, false));
		clockStepper = new ClockStepper(clock, 10);
		stepper = new InstructionStepper(clockStepper, cpu);
		design.prepareSimulation();
	}

	@Test
	public void testPcIncrement() {
		stepper.skipUntilFetching();
		Assert.assertEquals(0, cpu.getMemoryWordAddress().getValue().getBitsAsInt());
		stepper.step(0x000_00_093); // ADDI x1, x0, 0
		Assert.assertEquals(1, cpu.getMemoryWordAddress().getValue().getBitsAsInt());
		stepper.step(0x000_00_093); // ADDI x1, x0, 0
		Assert.assertEquals(2, cpu.getMemoryWordAddress().getValue().getBitsAsInt());
		stepper.step(0x000_00_093); // ADDI x1, x0, 0
		Assert.assertEquals(3, cpu.getMemoryWordAddress().getValue().getBitsAsInt());
	}

	@Test
	public void testAddi() {
		stepper.skipUntilFetching();
		Assert.assertEquals(0, cpu._registers.getMatrix().getRow(1).getBitsAsInt());
		stepper.step(0x005_00_093); // ADDI x1, x0, 5
		Assert.assertEquals(5, cpu._registers.getMatrix().getRow(1).getBitsAsInt());
		stepper.step(0x002_08_093); // ADDI x1, x1, 2
		Assert.assertEquals(7, cpu._registers.getMatrix().getRow(1).getBitsAsInt());
	}

	@Test
	public void testRegisterZero() {
		stepper.skipUntilFetching();
		Assert.assertEquals(0, cpu._registers.getMatrix().getRow(0).getBitsAsInt());
		stepper.step(0x005_00_013); // ADDI x0, x0, 5
		Assert.assertEquals(0, cpu._registers.getMatrix().getRow(0).getBitsAsInt());
	}

	@Test
	public void testAdd() {
		stepper.step(0x005_00_093); // ADDI x1, x0, 5
		stepper.step(0x004_00_113); // ADDI x2, x0, 4
		stepper.step(0x002_08_1b3); // ADD x3, x1, x2
		Assert.assertEquals(5, cpu._registers.getMatrix().getRow(1).getBitsAsInt());
		Assert.assertEquals(4, cpu._registers.getMatrix().getRow(2).getBitsAsInt());
		Assert.assertEquals(9, cpu._registers.getMatrix().getRow(3).getBitsAsInt());
	}

}
