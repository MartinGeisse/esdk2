package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
import name.martingeisse.esdk.library.util.RegisterBuilder;
import name.martingeisse.esdk.riscv.rtl.lan.LanController;
import name.martingeisse.esdk.riscv.rtl.lan.ReceiveBuffer;
import name.martingeisse.esdk.riscv.rtl.lan.SendBuffer;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {

		// compile the software
		CeeCompilerInvoker.invoke();

		// create design, realm, clock networks
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlBitSignalConnector clockSignalConnector = new RtlBitSignalConnector(realm);
		RtlClockNetwork clock = realm.createClockNetwork(clockSignalConnector);
		RtlBitSignalConnector ddrClock0SignalConnector = new RtlBitSignalConnector(realm);
		RtlClockNetwork ddrClock0 = realm.createClockNetwork(ddrClock0SignalConnector);
		RtlBitSignalConnector ddrClock90SignalConnector = new RtlBitSignalConnector(realm);
		RtlClockNetwork ddrClock90 = realm.createClockNetwork(ddrClock90SignalConnector);
		RtlBitSignalConnector ddrClock180SignalConnector = new RtlBitSignalConnector(realm);
		RtlClockNetwork ddrClock180 = realm.createClockNetwork(ddrClock180SignalConnector);
		RtlBitSignalConnector ddrClock270SignalConnector = new RtlBitSignalConnector(realm);
		RtlClockNetwork ddrClock270 = realm.createClockNetwork(ddrClock270SignalConnector);

		// create the main module, replacing certain sub-modules by synthesis-oriented ones
		ComputerModule.Implementation computerModule = new ComputerModule.Implementation(
				realm, clock, ddrClock0, ddrClock180, ddrClock270, ddrClock90) {

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

			@Override
			protected LanController createLanController(RtlRealm realm, RtlClockNetwork clk) {
				return new LanController.Implementation(realm, clk) {

					@Override
					protected ReceiveBuffer createReceiveBuffer(RtlRealm realm, RtlClockNetwork clk) {
						ReceiveBuffer.Connector connector = new ReceiveBuffer.Connector(realm, clk);
						RtlModuleInstance instance = new RtlModuleInstance(realm, "RAMB16_S4_S36");

						// port A (4k x 4)
						instance.createBitInputPort("WEA", connector.getWriteEnableSocket());
						instance.createBitInputPort("ENA", true);
						instance.createBitInputPort("SSRA", false);
						instance.createBitInputPort("CLKA", clk.getClockSignal());
						instance.createVectorInputPort("ADDRA", 12, connector.getWriteAddressSocket());
						instance.createVectorInputPort("DIA", 4, connector.getWriteDataSocket());

						// port B (512 x 32, internally 512 x 36)
						instance.createBitInputPort("WEB", false);
						instance.createBitInputPort("ENB", true);
						instance.createBitInputPort("SSRB", false);
						instance.createBitInputPort("CLKB", clk.getClockSignal());
						instance.createVectorInputPort("ADDRB", 9, connector.getReadAddressSocket());
						instance.createVectorInputPort("DIB", 32, RtlVectorConstant.of(realm, 32, 0));
						instance.createVectorInputPort("DIPB", 4, RtlVectorConstant.of(realm, 4, 0));
						connector.setReadDataSocket(instance.createVectorOutputPort("DOB", 32));

						return connector;
					}

					@Override
					protected SendBuffer createSendBuffer(RtlRealm realm, RtlClockNetwork clk) {
						SendBuffer.Connector connector = new SendBuffer.Connector(realm, clk);
						RtlModuleInstance instance = new RtlModuleInstance(realm, "RAMB16_S4_S36");

						// port A (4k x 4)
						instance.createBitInputPort("WEA", false);
						instance.createBitInputPort("ENA", true);
						instance.createBitInputPort("SSRA", false);
						instance.createBitInputPort("CLKA", clk.getClockSignal());
						instance.createVectorInputPort("ADDRA", 12, connector.getReadAddressSocket());
						instance.createVectorInputPort("DIA", 4, RtlVectorConstant.of(realm, 4, 0));
						connector.setReadDataSocket(instance.createVectorOutputPort("DOA", 4));

						// port B (512 x 32, internally 512 x 36)
						instance.createBitInputPort("WEB", connector.getWriteEnableSocket());
						instance.createBitInputPort("ENB", true);
						instance.createBitInputPort("SSRB", false);
						instance.createBitInputPort("CLKB", clk.getClockSignal());
						instance.createVectorInputPort("ADDRB", 9, connector.getWriteAddressSocket());
						instance.createVectorInputPort("DIB", 32, connector.getWriteDataSocket());
						instance.createVectorInputPort("DIPB", 4, RtlVectorConstant.of(realm, 4, 0));

						return connector;
					}

				};
			}
		};

		// load the bootloader into small memory
		try (FileInputStream in = new FileInputStream("riscv/resource/bootloader/build/program.bin")) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				computerModule._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				computerModule._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				computerModule._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				computerModule._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}

		// connect clk and reset
		RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset");
		RtlBitSignal reset = clkReset.createBitOutputPort("reset");
		clkReset.createBitInputPort("clk_in", clockPin(realm));
		clkReset.createBitInputPort("reset_in", buttonPin(realm, "V16"));
		computerModule.setReset(reset);
		ddrClock0SignalConnector.setConnected(withName(clkReset.createBitOutputPort("ddr_clk_0"), "ddr_clk_0"));
		ddrClock90SignalConnector.setConnected(withName(clkReset.createBitOutputPort("ddr_clk_90"), "ddr_clk_90"));
		ddrClock180SignalConnector.setConnected(withName(clkReset.createBitOutputPort("ddr_clk_180"), "ddr_clk_180"));
		ddrClock270SignalConnector.setConnected(withName(clkReset.createBitOutputPort("ddr_clk_270"), "ddr_clk_270"));
		clockSignalConnector.setConnected(ddrClock0SignalConnector.getConnected());

		// connect pixel display
		PixelDisplayController.Implementation displayController = (PixelDisplayController.Implementation)computerModule._display;
		VgaConnector.Connector vgaConnector = (VgaConnector.Connector) displayController._vgaConnector;
		vgaPin(realm, "H14", vgaConnector.getRSocket());
		vgaPin(realm, "H15", vgaConnector.getGSocket());
		vgaPin(realm, "G15", vgaConnector.getBSocket());
		vgaPin(realm, "F15", vgaConnector.getHsyncSocket());
		vgaPin(realm, "F14", vgaConnector.getVsyncSocket());

		// connect keyboard
		KeyboardController.Implementation keyboardController = (KeyboardController.Implementation)computerModule._keyboard;
		Ps2Connector.Connector ps2Connector = (Ps2Connector.Connector)keyboardController._ps2;
		ps2Connector.setClkSocket(ps2Pin(realm, "G14"));
		ps2Connector.setDataSocket(ps2Pin(realm, "G13"));

		// serial port test
		// I built my own software serial implementation because the built-in library for Arduino is buggy as hell.
		// Parameters: idle = HIGH, 1 start bit (LOW), 1 stop bit (HIGH, equivalent to at least one idle bit),
		// data is sent active-HIGH. Lowest bit is sent first. All bits including start/stop have the same length.
		// The catch is that this length is not well-defined and has to be measured:
		// - each pixel is two clock cycles
		// total logging length: 12.9cm, 512 pixels --> 4096 clocks
		// start bit length: 5.7cm (226 pixels, 452 clocks)
		// data bit length: 6.1 cm (242 pixels, 484 clocks)
		// residue after that: 1.2cm
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
		computerModule.setSerialPortSignal(serialPortSignal);
		RtlBitSignal serialPortActive = RegisterBuilder.build(false,
				clock, new RtlBitConstant(realm, true), serialPortSignal.not());
		RtlVectorSignal serialPortDivider = RegisterBuilder.build(8, VectorValue.of(8, 0),
				clock, r -> r.add(1));

		// connect LAN PHY interface
		{
			XilinxPinConfiguration configuration = new XilinxPinConfiguration();
			configuration.setIostandard("LVCMOS33");
			configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
			configuration.setDrive(8);
			RtlOutputPin mdcPin = new RtlOutputPin(realm);
			mdcPin.setId("P9");
			mdcPin.setConfiguration(configuration);
			mdcPin.setOutputSignal(computerModule.getLanMdc());
		}
		{
			XilinxPinConfiguration configuration = new XilinxPinConfiguration();
			configuration.setIostandard("LVCMOS33");
			configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
			configuration.setDrive(8);
			configuration.setAdditionalInfo("PULLUP");
			RtlBidirectionalPin mdioPin = new RtlBidirectionalPin(realm);
			mdioPin.setId("U5");
			mdioPin.setConfiguration(configuration);
			mdioPin.setOutputSignal(new RtlBitConstant(realm, false));
			mdioPin.setOutputEnableSignal(computerModule.getLanMdioOutWeak().not());
			computerModule.setLanMdioIn(mdioPin);
		}
		computerModule.setLanRxClk(inputPin(realm, "V3", "LVCMOS33"));
		computerModule.setLanRxDv(inputPin(realm, "V2", "LVCMOS33"));
		computerModule.setLanRxd(inputPin(realm, "V14", "LVCMOS33")
			.concat(inputPin(realm, "U11", "LVCMOS33"))
			.concat(inputPin(realm, "T11", "LVCMOS33"))
			.concat(inputPin(realm, "V8", "LVCMOS33"))
		);
		computerModule.setLanRxEr(inputPin(realm, "U14", "LVCMOS33"));
		computerModule.setLanTxClk(inputPin(realm, "T7", "LVCMOS33"));
		outputPin(realm, "P15", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, computerModule.getLanTxEn());
		outputPin(realm, "R11", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, computerModule.getLanTxd().select(0));
		outputPin(realm, "T15", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, computerModule.getLanTxd().select(1));
		outputPin(realm, "R5", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, computerModule.getLanTxd().select(2));
		outputPin(realm, "T5", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, computerModule.getLanTxd().select(3));
		outputPin(realm, "R6", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, computerModule.getLanTxEr());

		// connect buttons and switches -- shared by signal logger
		RtlVectorSignal buttonsAndSwitches = new RtlConcatenation(realm,
				slideSwitchPin(realm, "N17"), // switch 3
				slideSwitchPin(realm, "H18"), // switch 2
				slideSwitchPin(realm, "L14"), // switch 1
				slideSwitchPin(realm, "L13"), // switch 0
				buttonPin(realm, "V4"), // north
				buttonPin(realm, "H13"), // east
				buttonPin(realm, "K17"), // south
				buttonPin(realm, "D18") // west
		);
		computerModule.setButtonsAndSwitches(buttonsAndSwitches);

		// connect SPI
		{
			SpiInterface.Implementation spiInterface = (SpiInterface.Implementation)computerModule._spiInterface;
			SpiConnector.Connector connector = (SpiConnector.Connector)spiInterface._spiConnector;

			// general
			outputPin(realm, "U16", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, connector.getSckSocket()); // SCK
			outputPin(realm, "T4", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, connector.getMosiSocket()); // MOSI

			// DAC
		 	outputPin(realm, "N8", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, connector.getDacCsSocket()); // DAC_CS
		 	outputPin(realm, "P8", "LVCMOS33", 8, XilinxPinConfiguration.Slew.SLOW, true); // DAC_CLR

		}

		// connect unused SPI devices
		{
			outputPin(realm, "P11", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, false); // AD_CONV
			outputPin(realm, "N7", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, true); // AMP_CS
			outputPin(realm, "T3", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, false); // FPGA_INIT_B
			outputPin(realm, "M18", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, false); // LCD_E
			outputPin(realm, "L17", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, false); // LCD_RW
			outputPin(realm, "D16", "LVCMOS33", 4, XilinxPinConfiguration.Slew.SLOW, true); // SF_CE0
			outputPin(realm, "U3", "LVCMOS33", 6, XilinxPinConfiguration.Slew.SLOW, true); // SPI_SS_B
		}

		// connect signal logger
		SignalLoggerBusInterface.Connector loggerInterface = (SignalLoggerBusInterface.Connector)computerModule._signalLogger;
		SignalLogger signalLogger = new SignalLogger.Implementation(realm, clock, clock);
		signalLogger.setLogEnable(serialPortActive.and(serialPortDivider.compareEqual(0)));
		signalLogger.setLogData(RtlVectorConstant.of(realm, 28, 0).concat(buttonsAndSwitches.select(7, 4)));
		signalLogger.setBusEnable(loggerInterface.getBusEnableSocket());
		signalLogger.setBusWrite(loggerInterface.getBusWriteSocket());
		signalLogger.setBusWriteData(loggerInterface.getBusWriteDataSocket());
		loggerInterface.setBusReadDataSocket(signalLogger.getBusReadData());
		loggerInterface.setBusAcknowledgeSocket(signalLogger.getBusAcknowledge());

		// generate Verilog and ISE project files
		ProjectGenerator projectGenerator = new ProjectGenerator(realm, "TerminalTest", new File("ise/terminal_test"), "XC3S500E-FG320-4");
		projectGenerator.addVerilogFile(new File("riscv/resource/hdl/clk_reset.v"));
		projectGenerator.addUcfLine("NET \"pinC9\" PERIOD = 20.0ns HIGH 40%;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = D2;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = G4;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = J6;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = L5;");
		projectGenerator.addUcfLine("CONFIG PROHIBIT = R4;");
		projectGenerator.generate();

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

	private static RtlInputPin inputPin(RtlRealm realm, String id, String ioStandard) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard(ioStandard);
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

}
