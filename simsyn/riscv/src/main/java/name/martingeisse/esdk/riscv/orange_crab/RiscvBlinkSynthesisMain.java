package name.martingeisse.esdk.riscv.orange_crab;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
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
public class RiscvBlinkSynthesisMain {

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

		// use the pushbutton to reconfigure the FPGA
		RtlBitSignal button = inputPin(realm, "J17", "SSTL135_I");
		inOutPin(realm, "V17", "LVCMOS33", null, new RtlBitConstant(realm, false), button.not());

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

		// generate Verilog and ISE project files
		ProjectGenerator projectGenerator = new ProjectGenerator(realm, "RiscvBlink", new File("synthesize/riscv-blink"), "CSFBGA285");
		projectGenerator.clean();
		projectGenerator.generate();
		projectGenerator.build();
		projectGenerator.program();

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

	private static RtlBidirectionalPin inOutPin(RtlRealm realm, String id, String ioType, String slewRate, RtlBitSignal outputSignal, RtlBitSignal enableSignal) {
		LatticePinConfiguration configuration = new LatticePinConfiguration();
		configuration.set("IO_TYPE", ioType);
		if (slewRate != null) {
			configuration.set("SLEWRATE", slewRate);
		}
		RtlBidirectionalPin pin = new RtlBidirectionalPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		pin.setOutputEnableSignal(enableSignal);
		return pin;
	}

}
