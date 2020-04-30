package name.martingeisse.esdk.experiment;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;

import java.io.File;

/**
 *
 */
public class ColorTestMain {

	public static void main(String[] args) throws Exception {
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);

		// clk / reset
		RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset_highspeed");
		clkReset.createBitInputPort("clk_in", clockPin(realm));
		clkReset.createBitInputPort("reset_in", buttonPin(realm, "V16"));
		RtlBitSignal reset = clkReset.createBitOutputPort("reset");
		RtlClockNetwork clk0 = realm.createClockNetwork(withName(clkReset.createBitOutputPort("ddr_clk_0"), "ddr_clk_0"));
		RtlClockNetwork highspeedClk0 = realm.createClockNetwork(withName(clkReset.createBitOutputPort("highspeed_clk_0"), "highspeed_clk_0"));
		RtlClockNetwork highspeedClk180 = realm.createClockNetwork(withName(clkReset.createBitOutputPort("highspeed_clk_180"), "highspeed_clk_180"));

		// main module
		ColorTest colorTest = new ColorTest.Implementation(realm, clk0, highspeedClk0);

		// VGA pins
		vgaPin(realm, "H14", colorTest.getR());
		vgaPin(realm, "H15", colorTest.getG());
		vgaPin(realm, "G15", colorTest.getB());
		vgaPin(realm, "F15", colorTest.getHsync());
		vgaPin(realm, "F14", colorTest.getVsync());

		ProjectGenerator projectGenerator = new ProjectGenerator(realm, "ColorTest", new File("ise/color_test"), "XC3S500E-FG320-4");
		projectGenerator.addVerilogFile(new File("riscv/resource/hdl/clk_reset_highspeed.v"));
		projectGenerator.addUcfLine("NET \"pinC9\" PERIOD = 20.0ns HIGH 40%;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = D2;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = G4;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = J6;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = L5;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = R4;");
		projectGenerator.generate();
	}

	private static <T extends Item> T withName(T item, String name) {
		item.setName(name);
		return item;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId("C9");
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlInputPin buttonPin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setAdditionalInfo("PULLDOWN");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlOutputPin vgaPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setDrive(8);
		configuration.setSlew(XilinxPinConfiguration.Slew.FAST);
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

}
