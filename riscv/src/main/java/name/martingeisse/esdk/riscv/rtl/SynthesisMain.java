package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.synthesis.prettify.RtlPrettifier;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
import name.martingeisse.esdk.riscv.rtl.ram.RamControllerCore;
import name.martingeisse.esdk.riscv.rtl.ram.SdramConnector;
import name.martingeisse.esdk.riscv.rtl.ram.SdramConnectorImpl;
import name.martingeisse.esdk.riscv.rtl.terminal.KeyboardController;
import name.martingeisse.esdk.riscv.rtl.terminal.Ps2Connector;
import name.martingeisse.esdk.riscv.rtl.terminal.TextDisplayController;
import name.martingeisse.esdk.riscv.rtl.terminal.VgaConnector;

import java.io.File;

/**
 * simple RAM test yields "35, 0, 35"
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {
		ComputerDesign design = new ComputerDesign() {
			@Override
			protected ComputerModule.Implementation createComputerModule() {
				return new ComputerModule.Implementation(getRealm(), getClock(), getDdrClock0(), getDdrClock180(), getDdrClock270(), getDdrClock90()) {
					@Override
					protected RamControllerCore createBigRam() {
						return new name.martingeisse.esdk.riscv.rtl.ram.RamControllerCore.Implementation(getRealm(), _ddrClock0, _ddrClock180, _ddrClock270, _ddrClock90) {
							@Override
							protected SdramConnector createSdram() {
								return new SdramConnectorImpl(getRealm(), _clk0, _clk180, _clk270, _clk90);
							}
						};
					}
				};
			}
		};
		ComputerModule.Implementation computerModule = design.getComputerModule();
		RtlRealm realm = design.getRealm();

		RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset");
		RtlBitSignal reset = clkReset.createBitOutputPort("reset");
		RtlBitSignal mainClockSignal = clkReset.createBitOutputPort("clk");
		clkReset.createBitInputPort("clk_in", clockPin(realm));
		clkReset.createBitInputPort("reset_in", buttonPin(realm, "V4"));
		design.getClockSignalConnector().setConnected(mainClockSignal);
		computerModule.setReset(reset);
		design.getDdrClock0SignalConnector().setConnected(clkReset.createBitOutputPort("ddr_clk_0"));
		design.getDdrClock90SignalConnector().setConnected(clkReset.createBitOutputPort("ddr_clk_90"));
		design.getDdrClock180SignalConnector().setConnected(clkReset.createBitOutputPort("ddr_clk_180"));
		design.getDdrClock270SignalConnector().setConnected(clkReset.createBitOutputPort("ddr_clk_270"));

		TextDisplayController.Implementation displayController = (TextDisplayController.Implementation)computerModule._display;
		VgaConnector.Implementation vgaConnector = (VgaConnector.Implementation) displayController._vgaConnector;
		vgaPin(realm, "H14", vgaConnector.getR());
		vgaPin(realm, "H15", vgaConnector.getG());
		vgaPin(realm, "G15", vgaConnector.getB());
		vgaPin(realm, "F15", vgaConnector.getHsync());
		vgaPin(realm, "F14", vgaConnector.getVsync());

		KeyboardController.Implementation keyboardController = (KeyboardController.Implementation)computerModule._keyboard;
		Ps2Connector.Implementation ps2Connector = (Ps2Connector.Implementation)keyboardController._ps2;
		ps2Connector.setClk(ps2Pin(realm, "G14"));
		ps2Connector.setData(ps2Pin(realm, "G13"));

        //
		// signal logger
		//
		SignalLoggerBusInterface.Implementation loggerInterface = (SignalLoggerBusInterface.Implementation)computerModule._signalLogger;
		SignalLogger signalLogger = new SignalLogger.Implementation(realm, design.getClock(), design.getClock());
		signalLogger.setLogEnable(new RtlBitConstant(realm, false));
		signalLogger.setLogData(RtlVectorConstant.of(realm, 32, 0));
		signalLogger.setBusEnable(loggerInterface.getBusEnable());
		signalLogger.setBusWrite(loggerInterface.getBusWrite());
		signalLogger.setBusWriteData(loggerInterface.getBusWriteData());
		loggerInterface.setBusReadData(signalLogger.getBusReadData());
		loggerInterface.setBusAcknowledge(signalLogger.getBusAcknowledge());

		new RtlPrettifier().prettify(design.getRealm());
		ProjectGenerator projectGenerator = new ProjectGenerator(design.getRealm(), "TerminalTest", new File("ise/terminal_test"), "XC3S500E-FG320-4");
		projectGenerator.addVerilogFile(new File("riscv/resource/hdl/clk_reset.v"));
		projectGenerator.addUcfLine("NET \"pinC9\" PERIOD = 20.0ns HIGH 40%;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = D2;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = G4;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = J6;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = L5;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = R4;");
		projectGenerator.generate();
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
