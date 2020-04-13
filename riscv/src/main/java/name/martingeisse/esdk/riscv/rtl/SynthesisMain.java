package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
import name.martingeisse.esdk.library.util.RegisterBuilder;
import name.martingeisse.esdk.riscv.rtl.ram.RamController;
import name.martingeisse.esdk.riscv.rtl.ram.SdramConnector;
import name.martingeisse.esdk.riscv.rtl.ram.SdramConnectorImpl;
import name.martingeisse.esdk.riscv.rtl.spi.SpiConnector;
import name.martingeisse.esdk.riscv.rtl.spi.SpiInterface;
import name.martingeisse.esdk.riscv.rtl.terminal.KeyboardController;
import name.martingeisse.esdk.riscv.rtl.terminal.PixelDisplayController;
import name.martingeisse.esdk.riscv.rtl.terminal.Ps2Connector;
import name.martingeisse.esdk.riscv.rtl.terminal.VgaConnector;

import java.io.File;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {
		CeeCompilerInvoker.invoke();
		ComputerDesign design = new ComputerDesign() {
			@Override
			protected ComputerModule.Implementation createComputerModule() {
				return new ComputerModule.Implementation(getRealm(), getClock(), getDdrClock0(), getDdrClock180(), getDdrClock270(), getDdrClock90()) {

					@Override
					protected RamController createBigRam(RtlRealm realm, RtlClockNetwork ddrClock0, RtlClockNetwork ddrClock180, RtlClockNetwork ddrClock270, RtlClockNetwork ddrClock90) {
						return new name.martingeisse.esdk.riscv.rtl.ram.RamController.Implementation(realm, ddrClock0, ddrClock180, ddrClock270, ddrClock90) {
							@Override
							protected SdramConnector createSdram(RtlRealm realm, RtlClockNetwork clk0, RtlClockNetwork clk180, RtlClockNetwork clk270, RtlClockNetwork clk90) {
								return new SdramConnectorImpl(realm, clk0, clk180, clk270, clk90);
							}
						};
					}

					@Override
					protected PixelDisplayController createDisplay(RtlRealm realm, RtlClockNetwork clk) {
						return new PixelDisplayController.Implementation(realm, clk) {
							@Override
							protected VgaConnector createVgaConnector(RtlRealm realm) {
								return new VgaConnector.Connector(realm);
							}
						};
					}

					@Override
					protected KeyboardController createKeyboard(RtlRealm realm, RtlClockNetwork clk) {
						return new KeyboardController.Implementation(realm, clk) {
							@Override
							protected Ps2Connector createPs2(RtlRealm realm) {
								return new Ps2Connector.Connector(realm);
							}
						};
					}

					@Override
					protected SignalLoggerBusInterface createSignalLogger(RtlRealm realm) {
						return new SignalLoggerBusInterface.Connector(realm);
					}

					@Override
					protected SpiInterface createSpiInterface(RtlRealm realm, RtlClockNetwork clk) {
						return new SpiInterface.Implementation(getRealm(), clk) {
							@Override
							protected SpiConnector createSpiConnector(RtlRealm realm) {
								return new SpiConnector.Connector(realm);
							}
						};
					}

				};
			}
		};
		ComputerModule.Implementation computerModule = design.getComputerModule();
		RtlRealm realm = design.getRealm();

		// clk / reset
		RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset");
		RtlBitSignal reset = clkReset.createBitOutputPort("reset");
		clkReset.createBitInputPort("clk_in", clockPin(realm));
		clkReset.createBitInputPort("reset_in", buttonPin(realm, "V16"));
		computerModule.setReset(reset);
		design.getDdrClock0SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_0"), "ddr_clk_0"));
		design.getDdrClock90SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_90"), "ddr_clk_90"));
		design.getDdrClock180SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_180"), "ddr_clk_180"));
		design.getDdrClock270SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_270"), "ddr_clk_270"));
		design.getClockSignalConnector().setConnected(design.getDdrClock0SignalConnector().getConnected());

		// pixel display
		PixelDisplayController.Implementation displayController = (PixelDisplayController.Implementation)computerModule._display;
		VgaConnector.Connector vgaConnector = (VgaConnector.Connector) displayController._vgaConnector;
		vgaPin(realm, "H14", vgaConnector.getRSocket());
		vgaPin(realm, "H15", vgaConnector.getGSocket());
		vgaPin(realm, "G15", vgaConnector.getBSocket());
		vgaPin(realm, "F15", vgaConnector.getHsyncSocket());
		vgaPin(realm, "F14", vgaConnector.getVsyncSocket());

		// keyboard
		KeyboardController.Implementation keyboardController = (KeyboardController.Implementation)computerModule._keyboard;
		Ps2Connector.Connector ps2Connector = (Ps2Connector.Connector)keyboardController._ps2;
		ps2Connector.setClkSocket(ps2Pin(realm, "G14"));
		ps2Connector.setDataSocket(ps2Pin(realm, "G13"));

		// serial port test
		// Notes:
		// - idle state is HIGH
		// - start bit is LOW (seems to be somewhat longer than data bits! data is ~70% as long as start/stop)
		// - data gets transmitted active-HIGH
		// - lowest bit is sent first
		// - stop bit is HIGH (equivalent to "no stop bit, but at least 1 bit idle between bytes")
		// Nominally 115200 bauds means 868 clock cycles (at 100Mhz) per bit.
		// Measuring on screen looks like that is the length of a data bit.
		// Measuring again at smaller scale; 8 clock cycles per pixel:
		// total logging length: 12.9cm, 512 pixels --> 4096 clocks
		// start bit: 3.6cm --> 142.8837 pixels --> 1143.0696 clocks
		// data bits: 3 x 2.8cm --> 3 x 111.1318 pixels --> 3 x 889.0544 clocks
		// fraction of a data bit: 0.9cm --> 35.7209 pixels --> 285.7672 clocks
		RtlBitSignal serialPortSignal;
		{
			// NET "FX2_IO<5>"  LOC = "A6"  | IOSTANDARD = LVCMOS33  | SLEW = FAST  | DRIVE = 8 ;
			XilinxPinConfiguration configuration = new XilinxPinConfiguration();
			configuration.setIostandard("LVCMOS33");
			configuration.setSlew(XilinxPinConfiguration.Slew.FAST);
			configuration.setDrive(8);
			RtlInputPin serialPortPin = new RtlInputPin(realm);
			serialPortPin.setId("A6");
			serialPortPin.setConfiguration(configuration);
			serialPortSignal = serialPortPin;
		}
		RtlBitSignal serialPortActive = RegisterBuilder.build(false,
				design.getClock(), new RtlBitConstant(realm, true), serialPortSignal.not());
		RtlVectorSignal serialPortDivider = RegisterBuilder.build(3, VectorValue.of(3, 0),
				design.getClock(), r -> r.add(1));


        //
		// signal logger
		//
		SignalLoggerBusInterface.Connector loggerInterface = (SignalLoggerBusInterface.Connector)computerModule._signalLogger;
		SignalLogger signalLogger = new SignalLogger.Implementation(realm, design.getClock(), design.getClock());
		signalLogger.setLogEnable(serialPortActive.and(serialPortDivider.compareEqual(0)));
		signalLogger.setLogData(RtlVectorConstant.of(realm, 31, 0).concat(serialPortSignal));
		signalLogger.setBusEnable(loggerInterface.getBusEnableSocket());
		signalLogger.setBusWrite(loggerInterface.getBusWriteSocket());
		signalLogger.setBusWriteData(loggerInterface.getBusWriteDataSocket());
		loggerInterface.setBusReadDataSocket(signalLogger.getBusReadData());
		loggerInterface.setBusAcknowledgeSocket(signalLogger.getBusAcknowledge());

		//
		// GPIO (buttons and switches; LEDs not yet implemented)
		//
		computerModule.setButtonsAndSwitches(new RtlConcatenation(realm,
			slideSwitchPin(realm, "N17"), // switch 3
			slideSwitchPin(realm, "H18"), // switch 2
			slideSwitchPin(realm, "L14"), // switch 1
			slideSwitchPin(realm, "L13"), // switch 0
			buttonPin(realm, "V4"), // north
			buttonPin(realm, "H13"), // east
			buttonPin(realm, "K17"), // south
			buttonPin(realm, "D18") // west
		));

		// SPI
		{
			SpiInterface.Implementation spiInterface = (SpiInterface.Implementation)design.getComputerModule()._spiInterface;
			SpiConnector.Connector connector = (SpiConnector.Connector)spiInterface._spiConnector;

			// general
			outputPin(realm, "U16", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, connector.getSckSocket()); // SCK
			outputPin(realm, "T4", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, connector.getMosiSocket()); // MOSI

			// DAC
		 	outputPin(realm, "N8", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, connector.getDacCsSocket()); // DAC_CS
		 	outputPin(realm, "P8", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, true); // DAC_CLR

		}

		// unused SPI devices
		{
			outputPin(realm, "P11", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, false); // AD_CONV
			outputPin(realm, "N7", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, true); // AMP_CS
			outputPin(realm, "T3", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, false); // FPGA_INIT_B
			outputPin(realm, "M18", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, false); // LCD_E
			outputPin(realm, "L17", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, false); // LCD_RW
			outputPin(realm, "D16", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, true); // SF_CE0
			outputPin(realm, "U3", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, true); // SPI_SS_B
		}

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

	private static RtlInputPin slideSwitchPin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setAdditionalInfo("PULLUP");
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

	private static RtlOutputPin outputPin(RtlRealm realm, String id, String ioStandard, Integer drive, XilinxPinConfiguration.Slew slew, RtlBitSignal outputSignal) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard(ioStandard);
		configuration.setDrive(drive);
		configuration.setSlew(slew);
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlOutputPin outputPin(RtlRealm realm, String id, String ioStandard, Integer drive, XilinxPinConfiguration.Slew slew, boolean constant) {
		return outputPin(realm, id, ioStandard, drive, slew, new RtlBitConstant(realm, constant));
	}

}
