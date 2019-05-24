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
public class Computer extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final ComputerModule computerModule;

	public Computer() {
		this.realm = new RtlRealm(this);
		this.clock = realm.createClockNetwork(clockPin(realm));
		this.computerModule = new ComputerModule(realm, clock);

//		vgaPin(realm, "H14", computerModule._textDisplay._vgaConnector.getR());
//		vgaPin(realm, "H15", computerModule._textDisplay._vgaConnector.getG());
//		vgaPin(realm, "G15", computerModule._textDisplay._vgaConnector.getB());
//		vgaPin(realm, "F15", computerModule._textDisplay._vgaConnector.getHsync());
//		vgaPin(realm, "F14", computerModule._textDisplay._vgaConnector.getVsync());
//
//		computerModule._keyboard._ps2.setClk(ps2Pin(realm, "G14"));
//		computerModule._keyboard._ps2.setData(ps2Pin(realm, "G13"));

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public ComputerModule getComputerModule() {
		return computerModule;
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

	private static RtlInputPin ps2Pin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		configuration.setDrive(8);
		configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

}
