package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
import name.martingeisse.esdk.riscv.rtl.ram.RamController;
import name.martingeisse.esdk.riscv.rtl.ram.SdramConnector;
import name.martingeisse.esdk.riscv.rtl.ram.SdramConnectorImpl;
import name.martingeisse.esdk.riscv.rtl.spi.SpiConnector;
import name.martingeisse.esdk.riscv.rtl.spi.SpiInterface;
import name.martingeisse.esdk.riscv.rtl.terminal.KeyboardController;
import name.martingeisse.esdk.riscv.rtl.terminal.PixelDisplayController;
import name.martingeisse.esdk.riscv.rtl.terminal.Ps2Connector;
import name.martingeisse.esdk.riscv.rtl.terminal.VgaConnector;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {
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
		computerModule.setReset(new RtlBitConstant(realm, false));
		design.getDdrClock0SignalConnector().setConnected(new RtlBitConstant(realm, false));
		design.getDdrClock90SignalConnector().setConnected(new RtlBitConstant(realm, false));
		design.getDdrClock180SignalConnector().setConnected(new RtlBitConstant(realm, false));
		design.getDdrClock270SignalConnector().setConnected(new RtlBitConstant(realm, false));
		design.getClockSignalConnector().setConnected(new RtlBitConstant(realm, false));

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

		//
		// signal logger
		//
		SignalLoggerBusInterface.Connector loggerInterface = (SignalLoggerBusInterface.Connector)computerModule._signalLogger;
		SignalLogger signalLogger = new SignalLogger.Implementation(realm, design.getClock(), design.getClock());
		signalLogger.setLogEnable(new RtlBitConstant(realm, false));
		signalLogger.setLogData(RtlVectorConstant.of(realm, 32, 0));
		signalLogger.setBusEnable(loggerInterface.getBusEnableSocket());
		signalLogger.setBusWrite(loggerInterface.getBusWriteSocket());
		signalLogger.setBusWriteData(loggerInterface.getBusWriteDataSocket());
		loggerInterface.setBusReadDataSocket(signalLogger.getBusReadData());
		loggerInterface.setBusAcknowledgeSocket(signalLogger.getBusAcknowledge());

		//
		// GPIO (buttons and switches; LEDs not yet implemented)
		//
		computerModule.setButtonsAndSwitches(new RtlConcatenation(realm,
			new RtlBitConstant(realm, false), // switch 3
			new RtlBitConstant(realm, false), // switch 2
			new RtlBitConstant(realm, false), // switch 1
			new RtlBitConstant(realm, false), // switch 0
			new RtlBitConstant(realm, false), // north
			new RtlBitConstant(realm, false), // east
			new RtlBitConstant(realm, false), // south
			new RtlBitConstant(realm, false) // west
		));

		// SPI
		{
			SpiInterface.Implementation spiInterface = (SpiInterface.Implementation)design.getComputerModule()._spiInterface;
			SpiConnector.Connector connector = (SpiConnector.Connector)spiInterface._spiConnector;
		}







		design.getClockSignalConnector().setConnected(new RtlBitConstant(realm, false));
		new RtlClockGenerator(design.getClock(), 10);
		computerModule.setReset(new RtlBitConstant(realm, false));
		// prepareHighlevelDisplaySimulation(design);
		// prepareHdlDisplaySimulation(design);

		design.simulate();
	}

//	private static void prepareHighlevelDisplaySimulation(ComputerDesign design) {
//		TerminalPanel terminalPanel = new TerminalPanel(design.getClock());
//		((SimulatedTextDisplayController)design.getComputerModule()._textDisplay).setTerminalPanel(terminalPanel);
//		((SimulatedKeyboardController)design.getComputerModule()._keyboard).setTerminalPanel(terminalPanel);
//
//		JFrame frame = new JFrame("Terminal");
//		frame.add(terminalPanel);
//		frame.pack();
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setResizable(false);
//		frame.setVisible(true);
//		new Timer(500, event -> terminalPanel.repaint()).start();
//	}
//
//	private static void prepareHdlDisplaySimulation(ComputerDesign design) {
//		ComputerModule.Implementation computerModule = design.getComputerModule();
//		MyMonitorPanel monitorPanel = new MyMonitorPanel(design.getClock(), (TextDisplayController.Implementation) computerModule._textDisplay);
//
//		JFrame frame = new JFrame("Terminal");
//		frame.add(monitorPanel);
//		frame.pack();
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setResizable(false);
//		frame.setVisible(true);
//		new Timer(500, event -> monitorPanel.repaint()).start();
//	}

}
