package name.martingeisse.esdk.riscv.orange_crab;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.LatticePinConfiguration;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {

		// create design, realm, clock networks
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(clockPin(realm));
		Blink blink = new Blink.Implementation(realm, clock);
		outputPin(realm, "K4", "LVCMOS33", null, blink.getLedRn());
		outputPin(realm, "M3", "LVCMOS33", null, blink.getLedGn());
		outputPin(realm, "J3", "LVCMOS33", null, blink.getLedBn());

		// generate Verilog and ISE project files
		ProjectGenerator projectGenerator = new ProjectGenerator(realm, "Blink", new File("synthesize/blink"), "CSFBGA285");
		projectGenerator.clean();
		projectGenerator.generate();
		projectGenerator.build();
		projectGenerator.program();

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
