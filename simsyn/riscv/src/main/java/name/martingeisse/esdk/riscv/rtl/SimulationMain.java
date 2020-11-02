package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
import name.martingeisse.esdk.riscv.rtl.lan.LanController;
import name.martingeisse.esdk.riscv.rtl.pixel.SimulatedPixelDisplayPanel;
import name.martingeisse.esdk.riscv.rtl.ram.RamController;
import name.martingeisse.esdk.riscv.rtl.ram.SimulatedRam;
import name.martingeisse.esdk.riscv.rtl.ram.SimulatedRamAdapterWithoutRamdacSupport;
import name.martingeisse.esdk.riscv.rtl.simulation.SimulationDevice;
import name.martingeisse.esdk.riscv.rtl.simulation.SimulationDeviceDelegate;
import name.martingeisse.esdk.riscv.rtl.simulation.SimulationDeviceImpl;
import name.martingeisse.esdk.riscv.rtl.spi.SpiConnector;
import name.martingeisse.esdk.riscv.rtl.spi.SpiInterface;
import name.martingeisse.esdk.riscv.rtl.terminal.KeyboardController;
import name.martingeisse.esdk.riscv.rtl.terminal.PixelDisplayController;
import name.martingeisse.esdk.riscv.rtl.terminal.Ps2Connector;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class SimulationMain {

	private static Design design;
	private static RtlRealm realm;
	private static ComputerModule.Implementation computerModule;
	private static SimulatedRamAdapterWithoutRamdacSupport ramAdapter;
	private static SimulatedPixelDisplayPanel displayPanel;

	public static void main(String[] args) throws Exception {

		// compile the software
		CeeCompilerInvoker.invoke();

		// create design, realm, clock networks
		design = new Design();
		realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		RtlClockNetwork ddrClock0 = realm.createClockNetwork(new RtlBitConstant(realm, false));
		RtlClockNetwork ddrClock90 = realm.createClockNetwork(new RtlBitConstant(realm, false));
		RtlClockNetwork ddrClock180 = realm.createClockNetwork(new RtlBitConstant(realm, false));
		RtlClockNetwork ddrClock270 = realm.createClockNetwork(new RtlBitConstant(realm, false));

		// create the main module, replacing certain sub-modules by synthesis-oriented ones
		computerModule = new ComputerModule.Implementation(realm, clock, ddrClock0, ddrClock180, ddrClock270, ddrClock90) {

			@Override
			protected RamController createBigRam(RtlRealm realm, RtlClockNetwork ddrClock0, RtlClockNetwork ddrClock180, RtlClockNetwork ddrClock270, RtlClockNetwork ddrClock90) {
				// Do NOT use any DDR clock here -- those won't get driven by the simulation.
				// For now, we don't use delayed responses, but we should do that before moving to real
				// hardware again, so we know they are handled correctly.
				return new SimulatedRamAdapterWithoutRamdacSupport(realm, _clk);
			}

			@Override
			protected PixelDisplayController createDisplay(RtlRealm realm, RtlClockNetwork clk) {
				PixelDisplayController.Connector dummy = new PixelDisplayController.Connector(realm, clk);
				dummy.setRamdacRequestEnableSocket(new RtlBitConstant(realm, false));
				dummy.setRamdacRequestWordAddressSocket(RtlVectorConstant.of(realm, 24, 0));
				return dummy;
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
			protected SimulationDevice createSimulationDevice(RtlRealm realm, RtlClockNetwork clk) {
				return new SimulationDeviceImpl(realm, clk, new SimulationDeviceDelegate() {

					@Override
					public int read(int wordAddress) {
						return readFromSimulationDevice(wordAddress);
					}

					@Override
					public void write(int wordAddress, int byteMask, int data) {
						writeToSimulationDevice(wordAddress, byteMask, data);
					}

				});
			}

			@Override
			protected LanController createLanController(RtlRealm realm, RtlClockNetwork clk) {
				LanController.Connector connector = new LanController.Connector(realm, clk);
				connector.setBusAcknowledgeSocket(new RtlBitConstant(realm, true));
				connector.setBusReadDataSocket(RtlVectorConstant.of(realm, 32, 0));
				connector.setMdcSocket(new RtlBitConstant(realm, false));
				connector.setMdioOutWeakSocket(new RtlBitConstant(realm, false));
				connector.setTxEnSocket(new RtlBitConstant(realm, false));
				connector.setTxErSocket(new RtlBitConstant(realm, false));
				connector.setTxdSocket(RtlVectorConstant.of(realm, 4, 0));
				return connector;
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

		// connect reset
		computerModule.setReset(new RtlBitConstant(realm, false));

		// connect pixel display
		ramAdapter = (SimulatedRamAdapterWithoutRamdacSupport) computerModule._bigRam;
		displayPanel = new SimulatedPixelDisplayPanel(ramAdapter.getRam());

		JFrame frame = new JFrame("Graphics Display");
		frame.add(displayPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> displayPanel.repaint()).start();

		// connect keyboard
		KeyboardController.Implementation keyboardController = (KeyboardController.Implementation)computerModule._keyboard;
		Ps2Connector.Connector ps2Connector = (Ps2Connector.Connector)keyboardController._ps2;
		ps2Connector.setClkSocket(new RtlBitConstant(realm, true));
		ps2Connector.setDataSocket(new RtlBitConstant(realm, true));

		// serial port is not connected
		computerModule.setSerialPortSignal(new RtlBitConstant(realm, true));

		// connect LAN PHY interface
		computerModule.setLanMdioIn(new RtlBitConstant(realm, false));
		computerModule.setLanRxClk(new RtlBitConstant(realm, false));
		computerModule.setLanRxDv(new RtlBitConstant(realm, false));
		computerModule.setLanRxd(RtlVectorConstant.of(realm, 4, 0));
		computerModule.setLanRxEr(new RtlBitConstant(realm, false));
		computerModule.setLanTxClk(new RtlBitConstant(realm, false));

		// connect buttons and switches -- shared by signal logger
		RtlVectorSignal buttonsAndSwitches = new RtlConcatenation(realm,
				new RtlBitConstant(realm, false), // switch 3
				new RtlBitConstant(realm, false), // switch 2
				new RtlBitConstant(realm, false), // switch 1
				new RtlBitConstant(realm, false), // switch 0
				new RtlBitConstant(realm, false), // north
				new RtlBitConstant(realm, false), // east
				new RtlBitConstant(realm, false), // south
				new RtlBitConstant(realm, false) // west
		);
		computerModule.setButtonsAndSwitches(buttonsAndSwitches);

		// connect signal logger
		SignalLoggerBusInterface.Connector loggerInterface = (SignalLoggerBusInterface.Connector)computerModule._signalLogger;
		SignalLogger signalLogger = new SignalLogger.Implementation(realm, clock, clock);
		signalLogger.setLogEnable(new RtlBitConstant(realm, false));
		signalLogger.setLogData(RtlVectorConstant.of(realm, 32, 0));
		signalLogger.setBusEnable(loggerInterface.getBusEnableSocket());
		signalLogger.setBusWrite(loggerInterface.getBusWriteSocket());
		signalLogger.setBusWriteData(loggerInterface.getBusWriteDataSocket());
		loggerInterface.setBusReadDataSocket(signalLogger.getBusReadData());
		loggerInterface.setBusAcknowledgeSocket(signalLogger.getBusAcknowledge());

		// set debugger breakpoints here to allow clock stepping
		new RtlClockedSimulationItem(clock) {

			@Override
			public void computeNextState() {
			}

			@Override
			public void updateState() {
			}

		};

		// simulate
		new RtlClockGenerator(clock, 10);
		design.simulate();

	}

	private static int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

	private static <T extends Item> T withName(T item, String name) {
		item.setName(name);
		return item;
	}

	//region simulation device and helpers

	public static int readFromSimulationDevice(int wordAddress) {
		switch (wordAddress) {

			case 0:
				// return 1 to show that this is a simulation
				return 1;

			default:
				return 0;

		}
	}

	public static void writeToSimulationDevice(int wordAddress, int byteMask, int data) {
		switch (wordAddress) {

			case 0:
				design.stopSimulation();
				break;

			case 1:
				debugPrint(data);
				break;

			case 2:
				memoryHelper(data);
				break;

			case 3:
				displayPanel.setDisplayPlane(data & 1);
				break;

		}
	}

	private static void debugPrint(int subcode) {
		Multicycle.Implementation cpu = (Multicycle.Implementation)computerModule._cpu;
		System.out.print("OUT:        ");
		int a0 = cpu._registers.getMatrix().getRow(10).getBitsAsInt();
		int a1 = cpu._registers.getMatrix().getRow(11).getBitsAsInt();
		switch (subcode) {

			case 0:
				System.out.println(readZeroTerminatedMemoryString(a0));
				break;

			case 1: {
				System.out.println(readZeroTerminatedMemoryString(a0) + ": " + a1 + " (0x" + Integer.toHexString(a1) + ")");
				break;
			}

			default:
				System.out.println("???");
		}
	}

	private static void memoryHelper(int subcode) {
		Multicycle.Implementation cpu = (Multicycle.Implementation)computerModule._cpu;
		int a0 = cpu._registers.getMatrix().getRow(10).getBitsAsInt() & 0x0fffffff;
		int a1 = cpu._registers.getMatrix().getRow(11).getBitsAsInt();
		int a2 = cpu._registers.getMatrix().getRow(12).getBitsAsInt();
		SimulatedRam.Implementation ram = ramAdapter.getRam();
		switch (subcode) {

			// fill words
			case 0: {
				int wordAddress = (a0 >> 2);
				ram._memory0.getMatrix().setRows(wordAddress, wordAddress + a2, VectorValue.of(8, a1 & 0xff));
				ram._memory1.getMatrix().setRows(wordAddress, wordAddress + a2, VectorValue.of(8, (a1 >> 8) & 0xff));
				ram._memory2.getMatrix().setRows(wordAddress, wordAddress + a2, VectorValue.of(8, (a1 >> 16) & 0xff));
				ram._memory3.getMatrix().setRows(wordAddress, wordAddress + a2, VectorValue.of(8, (a1 >> 24) & 0xff));
				break;
			}

			default:
				System.out.println("invalid memoryHelper subcode: " + subcode);
		}
	}

	public static byte readMemoryByte(int address) {
		int wordAddress = (address & 0x0fffffff) >> 2;
		int byteOffset = (address & 3);
		if (address < 0) {
			SimulatedRam.Implementation ram = ramAdapter.getRam();
			RtlProceduralMemory memory = (byteOffset == 0 ? ram._memory0 : byteOffset == 1 ? ram._memory1 :
					byteOffset == 2 ? ram._memory2 : ram._memory3);
			return (byte)memory.getMatrix().getRow(wordAddress).getAsUnsignedInt();
		} else {
			RtlProceduralMemory memory = (byteOffset == 0 ? computerModule._memory0 : byteOffset == 1 ? computerModule._memory1 :
					byteOffset == 2 ? computerModule._memory2 : computerModule._memory3);
			return (byte)memory.getMatrix().getRow(wordAddress).getAsUnsignedInt();
		}
	}

	public static byte[] readMemoryBytes(int startAddress, int count) {
		byte[] result = new byte[count];
		for (int i = 0; i < count; i++) {
			result[i] = readMemoryByte(startAddress + i);
		}
		return result;
	}

	public static String readMemoryString(int startAddress, int count) {
		return new String(readMemoryBytes(startAddress, count), StandardCharsets.ISO_8859_1);
	}

	public static String readZeroTerminatedMemoryString(int startAddress) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		while (true) {
			byte b = readMemoryByte(startAddress);
			if (b == 0) {
				break;
			}
			stream.write(b);
			startAddress++;
		}
		return new String(stream.toByteArray(), StandardCharsets.ISO_8859_1);
	}

	//endregion

}
