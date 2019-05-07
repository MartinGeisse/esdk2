package name.martingeisse.esdk.riscv.implementation;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
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
	private final Multicycle cpu;
	private final RtlSimulatedSettableVectorSignal instruction;
	private final ClockStepper clockStepper;
	private final InstructionStepper stepper;

	public SimpleTests() {
		design = new Design();
		realm = new RtlRealm(design);
		clock = new RtlClockNetwork(realm);
		cpu = new Multicycle(realm, clock);
		instruction = new RtlSimulatedSettableVectorSignal(realm, 32);
		clockStepper = new ClockStepper(clock, 10);
		stepper = new InstructionStepper(clockStepper, cpu);
		cpu.setInstructionReadAcknowledge(new RtlBitConstant(realm, true));
		cpu.setInstruction(instruction);
		cpu.setMemoryAcknowledge(new RtlBitConstant(realm, true));
		cpu.setMemoryReadData(RtlVectorConstant.ofUnsigned(realm, 32, 0));
		design.prepareSimulation();
	}

	@Test
	public void testPcIncrement() {
		instruction.setValue(VectorValue.ofUnsigned(32, 0x000_00_093)); // ADDI x1, x0, 0
		stepper.skipUntilFetching();
		Assert.assertEquals(0, cpu.getInstructionAddress().getValue().getBitsAsInt());
		stepper.step();
		Assert.assertEquals(4, cpu.getInstructionAddress().getValue().getBitsAsInt());
		stepper.step();
		Assert.assertEquals(8, cpu.getInstructionAddress().getValue().getBitsAsInt());
		stepper.step();
		Assert.assertEquals(12, cpu.getInstructionAddress().getValue().getBitsAsInt());
	}


	@Test
	public void testAddi() {
		instruction.setValue(VectorValue.ofUnsigned(32, 0x005_00_093)); // ADDI x1, x0, 5
		stepper.skipUntilFetching();
		Assert.assertEquals(0, cpu.registers.getMatrix().getRow(1).getBitsAsInt());
		stepper.step();
		Assert.assertEquals(5, cpu.registers.getMatrix().getRow(1).getBitsAsInt());
		instruction.setValue(VectorValue.ofUnsigned(32, 0x002_08_093)); // ADDI x1, x1, 2
		stepper.step();
		Assert.assertEquals(7, cpu.registers.getMatrix().getRow(1).getBitsAsInt());
	}

}