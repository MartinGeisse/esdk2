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
	private final InstructionStepper stepper;

	public SimpleTests() {
		design = new Design();
		realm = new RtlRealm(design);
		clock = new RtlClockNetwork(realm);
		cpu = new Multicycle(realm, clock);
		instruction = new RtlSimulatedSettableVectorSignal(realm, 32);
		stepper = new InstructionStepper(new ClockStepper(clock, 10), cpu);
		cpu.setInstructionReadAcknowledge(new RtlBitConstant(realm, true));
		cpu.setInstruction(instruction);
		cpu.setMemoryAcknowledge(new RtlBitConstant(realm, true));
		cpu.setMemoryReadData(RtlVectorConstant.ofUnsigned(realm, 32, 0));
		design.prepareSimulation();
	}

	@Test
	public void testPcIncrement() {
		instruction.setValue(VectorValue.ofUnsigned(32, 0x00000093)); // ADDI x1, x0, 0
		stepper.skipUntilFetching();
		Assert.assertEquals(0, cpu.getInstructionAddress().getValue().getBitsAsInt());
		stepper.step();
		Assert.assertEquals(4, cpu.getInstructionAddress().getValue().getBitsAsInt());
		stepper.step();
		Assert.assertEquals(8, cpu.getInstructionAddress().getValue().getBitsAsInt());
		stepper.step();
		Assert.assertEquals(12, cpu.getInstructionAddress().getValue().getBitsAsInt());
	}

}
