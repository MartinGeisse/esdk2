package name.martingeisse.esdk.riscv.orange_crab;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.LatticePinConfiguration;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class TestbildSynthesisMain {

	public static void main(String[] args) throws Exception {

		// create design and realm
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);

		// create clock network
		RtlModuleInstance pll = new RtlModuleInstance(realm, "EHXPLLL");
		// ports
		pll.createBitInputPort("CLKI", clockPin(realm));
//		pll.createBitInputPort("CLKI2", false);
//		pll.createBitInputPort("SEL", false);
//		pll.createBitInputPort("CLKFB", false);
//		pll.createVectorInputPort("PHASESEL", 2, RtlVectorConstant.of(realm, 2, 0));
//		pll.createBitInputPort("PHASEDIR", false);
//		pll.createBitInputPort("PHASESTEP", false);
//		pll.createBitInputPort("PHASELOADREG", false);
		pll.createBitInputPort("STDBY", false);
		pll.createBitInputPort("RST", false);
		pll.createBitInputPort("ENCLKOP", true);
//		pll.createBitInputPort("ENCLKOS", false);
//		pll.createBitInputPort("ENCLKOS2", false);
		pll.createBitInputPort("ENCLKOS3", true); // not clear if this is needed when using S3 as feedback, probably not
		RtlClockNetwork clock = realm.createClockNetwork(pll.createBitOutputPort("CLKOP"));
		// "make it work" attributes and parameters
		pll.getAttributes().put("FREQUENCY_PIN_CLKI", 48);
		// The following settings are seemingly used by foboot, but for some reason cause extremely long startup times.
		// Since the attributes are not documented, nobody knows why.
//		pll.getAttributes().put("ICP_CURRENT", "6");
//		pll.getAttributes().put("LPF_RESISTOR", "16");
//		pll.getAttributes().put("MFG_ENABLE_FILTEROPAMP", "1");
//		pll.getAttributes().put("MFG_GMCREF_SEL", "2");
		// input clock
		pll.getParameters().put("CLKI_DIV", 2);
		// output clock
		pll.getParameters().put("CLKOP_ENABLE", "ENABLED");
		pll.getParameters().put("CLKOP_DIV", 12);
		pll.getParameters().put("CLKOP_FPHASE", 0);
		pll.getParameters().put("CLKOP_CPHASE", 0);
		// feedback clock
		pll.getParameters().put("FEEDBK_PATH", "INT_OS3");
		pll.getParameters().put("CLKOS3_ENABLE", "ENABLED");
		pll.getParameters().put("CLKOS3_DIV", 1);
		pll.getParameters().put("CLKOS3_FPHASE", 0);
		pll.getParameters().put("CLKOS3_CPHASE", 0);
		pll.getParameters().put("CLKFB_DIV", 25);

		// create main RTL
		Testbild.Implementation testbild = new Testbild.Implementation(realm, clock);

		// LED pins
		outputPin(realm, "K4", "LVCMOS33", null, testbild.getLedRn());
		outputPin(realm, "M3", "LVCMOS33", null, testbild.getLedGn());
		outputPin(realm, "J3", "LVCMOS33", null, testbild.getLedBn());

		// VGA pins
		outputPin(realm, "C8", "LVCMOS33", null, testbild.getR().select(1)); // IO 9
		outputPin(realm, "B8", "LVCMOS33", null, testbild.getG().select(1)); // IO 10
		outputPin(realm, "A8", "LVCMOS33", null, testbild.getB().select(1)); // IO 11
		outputPin(realm, "H2", "LVCMOS33", null, testbild.getHsync()); // IO 12
		outputPin(realm, "J2", "LVCMOS33", null, testbild.getVsync()); // IO 13
		outputPin(realm, "N17", "LVCMOS33", null, testbild.getR().select(0)); // IO 0
		outputPin(realm, "M18", "LVCMOS33", null, testbild.getG().select(0)); // IO 1
		outputPin(realm, "B9", "LVCMOS33", null, testbild.getB().select(0)); // IO 6

		// use the pushbutton to reconfigure the FPGA
		RtlBitSignal button = inputPin(realm, "J17", "SSTL135_I");
		inOutPin(realm, "V17", "LVCMOS33", null, new RtlBitConstant(realm, false), button.not());

		// generate Verilog and ISE project files
		ProjectGenerator projectGenerator = new ProjectGenerator(realm, "Testbild", new File("synthesize/testbild"), "CSFBGA285");
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
