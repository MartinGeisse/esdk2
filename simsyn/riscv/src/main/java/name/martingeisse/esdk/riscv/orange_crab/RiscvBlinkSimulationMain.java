package name.martingeisse.esdk.riscv.orange_crab;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlIntervalSimulationItem;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.LatticePinConfiguration;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.ProjectGenerator;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.riscv.rtl.CeeCompilerInvoker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class RiscvBlinkSimulationMain {

	public static void main(String[] args) throws Exception {

		CeeCompilerInvoker.invoke("riscv/resource/lattice-riscv-blink");

		// create design, realm, clock networks
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(clockPin(realm));
		RiscvBlink.Implementation riscvBlink = new RiscvBlink.Implementation(realm, clock);
		outputPin(realm, "K4", "LVCMOS33", null, riscvBlink.getLedRn());
		outputPin(realm, "M3", "LVCMOS33", null, riscvBlink.getLedGn());
		outputPin(realm, "J3", "LVCMOS33", null, riscvBlink.getLedBn());

		// load the program into small memory
		try (FileInputStream in = new FileInputStream("riscv/resource/lattice-riscv-blink/build/program.bin")) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				riscvBlink._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				riscvBlink._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				riscvBlink._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				riscvBlink._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}

		// set debugger breakpoints here to allow clock stepping
		new RtlClockedSimulationItem(clock) {

			@Override
			public void computeNextState() {
			}

			@Override
			public void updateState() {
			}

		};

		// simulate
		new RtlClockGenerator(clock, 21); // ca. 48 MHz expressed in nanoseconds
		new RtlIntervalSimulationItem(realm, 100_000_000, () -> System.out.println(riscvBlink._ledState.getValue().getBitsAsInt()));
		design.simulate();


	}

	private static int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

	private static <T extends Item> T withName(T item, String name) {
		item.setName(name);
		return item;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		RtlInputPin pin = inputPin(realm, "A9", "LVCMOS33");
		((LatticePinConfiguration)pin.getConfiguration()).setFrequency("48.0 MHz");
		return pin;
	}

	private static RtlInputPin inputPin(RtlRealm realm, String id, String ioType) {
		LatticePinConfiguration configuration = new LatticePinConfiguration();
		configuration.set("IO_TYPE", ioType);
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlOutputPin outputPin(RtlRealm realm, String id, String ioType, String slewRate, RtlBitSignal outputSignal) {
		LatticePinConfiguration configuration = new LatticePinConfiguration();
		configuration.set("IO_TYPE", ioType);
		if (slewRate != null) {
			configuration.set("SLEWRATE", slewRate);
		}
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

}
