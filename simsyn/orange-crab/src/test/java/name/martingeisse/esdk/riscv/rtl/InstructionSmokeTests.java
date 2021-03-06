package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.util.ClockStepper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class InstructionSmokeTests {

	private final Design design;
	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final Multicycle.Implementation cpu;
	private final ClockStepper clockStepper;
	private final InstructionStepper stepper;

	public InstructionSmokeTests() {
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

//region helper methods

	private void setRegisters(int... values) {
		for (int i = 0; i < values.length; i++) {
			cpu._registers.getMatrix().setRow(i + 1, VectorValue.of(32, values[i] & 0xffff_ffffL));
		}
	}

	private void assertRegisters(int... values) {
		for (int i = 0; i < values.length; i++) {
			int value = cpu._registers.getMatrix().getRow(i + 1).getBitsAsInt();
			Assert.assertEquals(values[i], value);
		}
	}

	private void testOpImm(int funct3, int registerValue, int immediateValue, int expectedResult) {
		setRegisters(registerValue);
		stepper.step((immediateValue << 20) | (1 << 15) | (funct3 << 12) | (2 << 7) | 19);
		assertRegisters(registerValue, expectedResult);
	}

//endregion

	@Test
	public void testAddi() {
		testOpImm(0, 53, 3, 56);
		testOpImm(0, -10, 20, 10);
		testOpImm(0, 20, -10, 10);
	}

	@Test
	public void testSlli() {
		testOpImm(1, 1, 3, 8);
		testOpImm(1, 5, 2, 20);
		testOpImm(1, -1, 2, -4);
	}

	@Test
	public void testSlti() {
		testOpImm(2, 5, 10, 1);
		testOpImm(2, 10, 5, 0);
		testOpImm(2, 10, 10, 0);
		testOpImm(2, -6, -3, 1);
		testOpImm(2, -3, 3, 1);
	}

	@Test
	public void testSltiu() {
		testOpImm(3, 5, 10, 1);
		testOpImm(3, 10, 5, 0);
		testOpImm(3, 10, 10, 0);
		testOpImm(3, -6, -3, 1);
		testOpImm(3, -3, 3, 0);
	}

	@Test
	public void testXori() {
		testOpImm(4, 6, 12, 10);
	}

	@Test
	public void testOri() {
		testOpImm(6, 6, 12, 14);
	}

	@Test
	public void testAndi() {
		testOpImm(7, 6, 12, 4);
	}

	private void testSwHelper(int immediateAddress, int addressRegister, int expectedAddress) {
		testSwHelper(immediateAddress, addressRegister, expectedAddress, 10);
		testSwHelper(immediateAddress, addressRegister, expectedAddress, -10);
	}

	private void testSwHelper(int instructionImmediateBits, int addressRegister, int expectedWordAddress, int data) {
		// rs1 = x1 = address
		// rs2 = x2 = data
		StoreRecorder storeRecorder = new StoreRecorder(clock);
		setRegisters(addressRegister, data);
		stepper.step(instructionImmediateBits | 0x002_0a_023);
		assertRegisters(addressRegister, data);
		Assert.assertEquals(1, storeRecorder.entries.size());
		storeRecorder.entries.get(0).assertEquals(expectedWordAddress & 0x3fff_ffff, data, 15);
	}

	@Test
	public void testSw() {
		testSwHelper(0x000_00_000, 20, 5);
		testSwHelper(0x000_00_000, -20, -5);
		testSwHelper(0x000_00_000, 21, 5);
		testSwHelper(0x000_00_000, -19, -5);
		testSwHelper(0x000_00_180, 20, 5);
		testSwHelper(0x000_00_180, 21, 6);
	}

	@Test
	public void tetestBeqTaken() {
		setRegisters(5, 5);
		stepper.step(0x102_08_063);
		assertRegisters(5, 5);
		Assert.assertNotEquals(4, cpu._pc.getValue().getBitsAsInt());
	}

	@Test
	public void testBeqNotTaken() {
		setRegisters(5, 7);
		stepper.step(0x102_08_063);
		assertRegisters(5, 7);
		Assert.assertEquals(4, cpu._pc.getValue().getBitsAsInt());
	}

//region helper classes

	public class StoreRecorder extends RtlClockedSimulationItem {

		public final List<StoreRecorderEntry> entries = new ArrayList<>();
		private StoreRecorderEntry currentEntry;

		public StoreRecorder(RtlClockNetwork clockNetwork) {
			super(clockNetwork);
		}

		@Override
		public void computeNextState() {
			if (cpu.getBusEnable().getValue() && cpu.getBusWrite().getValue()) {
				currentEntry = new StoreRecorderEntry();
				currentEntry.wordAddress = cpu.getBusWordAddress().getValue().getBitsAsInt();
				currentEntry.data = cpu.getBusWriteData().getValue().getBitsAsInt();
				currentEntry.mask = cpu.getBusWriteMask().getValue().getBitsAsInt();
			} else {
				currentEntry = null;
			}
		}

		@Override
		public void updateState() {
			if (currentEntry != null) {
				entries.add(currentEntry);
			}
		}

	}

	public static class StoreRecorderEntry {

		public int wordAddress;
		public int data;
		public int mask;

		public void assertEquals(int wordAddress, int data, int mask) {
			Assert.assertEquals(wordAddress, this.wordAddress);
			Assert.assertEquals(data, this.data);
			Assert.assertEquals(mask, this.mask);
		}

	}

//endregion

}
