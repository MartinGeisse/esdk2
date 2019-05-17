package name.martingeisse.esdk.riscv.experiment;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;

/**
 *
 */
public class DisplayTestDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final DisplayTest displayTest;

	public DisplayTestDesign() {
		this.realm = new RtlRealm(this);
		this.clock = realm.createClockNetwork(clockPin(realm));
		this.displayTest = new DisplayTest(realm, clock);

		vgaPin(realm, "H14", displayTest._textDisplay._vgaConnector.getR());
		vgaPin(realm, "H15", displayTest._textDisplay._vgaConnector.getG());
		vgaPin(realm, "G15", displayTest._textDisplay._vgaConnector.getB());
		vgaPin(realm, "F15", displayTest._textDisplay._vgaConnector.getHsync());
		vgaPin(realm, "F14", displayTest._textDisplay._vgaConnector.getVsync());

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public DisplayTest getDisplayTest() {
		return displayTest;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId("C9");
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
