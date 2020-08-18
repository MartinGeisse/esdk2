package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralMemory;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
import name.martingeisse.esdk.riscv.rtl.pixel.SimulatedOpenglHelper;
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
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class SimulationMain {

    public static final boolean LWJGL = false;

    private final ComputerDesign design;
    private final ComputerModule.Implementation computerModule;
    private final RtlRealm realm;
    private final SimulatedRamAdapterWithoutRamdacSupport ramAdapter;
    private final SimulatedPixelDisplayPanel displayPanel;
    private final KeyboardController.Implementation keyboardController;
    private final Ps2Connector.Connector ps2Connector;
    private final SignalLoggerBusInterface.Connector loggerInterface;
    private final SignalLogger signalLogger;
    private final SimulatedOpenglHelper openglHelper;

    public SimulationMain() throws Exception {
        design = new ComputerDesign("riscv/resource/gfx-program/build/program.bin") {
            @Override
            protected ComputerModule.Implementation createComputerModule() {
                return new ComputerModule.Implementation(getRealm(), getClock(), getDdrClock0(), getDdrClock180(), getDdrClock270(), getDdrClock90()) {

                    @Override
                    protected RamController createBigRam(RtlRealm realm, RtlClockNetwork ddrClock0, RtlClockNetwork ddrClock180, RtlClockNetwork ddrClock270, RtlClockNetwork ddrClock90) {
                        // do NOT use any DDR clock here -- those won't get driven by the simulation
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
                };
            }
        };
        computerModule = design.getComputerModule();
        realm = design.getRealm();

        // clk / reset
        computerModule.setReset(new RtlBitConstant(realm, false));
        design.getDdrClock0SignalConnector().setConnected(new RtlBitConstant(realm, false));
        design.getDdrClock90SignalConnector().setConnected(new RtlBitConstant(realm, false));
        design.getDdrClock180SignalConnector().setConnected(new RtlBitConstant(realm, false));
        design.getDdrClock270SignalConnector().setConnected(new RtlBitConstant(realm, false));
        design.getClockSignalConnector().setConnected(new RtlBitConstant(realm, false));

        // pixel display
        ramAdapter = (SimulatedRamAdapterWithoutRamdacSupport) computerModule._bigRam;
        displayPanel = new SimulatedPixelDisplayPanel(ramAdapter.getRam());

        if (LWJGL) {
            openglHelper = new SimulatedOpenglHelper();
        } else {
            JFrame frame = new JFrame("Terminal");
            frame.add(displayPanel);
            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setVisible(true);
            new Timer(500, event -> displayPanel.repaint()).start();
            openglHelper = null;
        }

        // keyboard (disable for now)
        keyboardController = (KeyboardController.Implementation) computerModule._keyboard;
        ps2Connector = (Ps2Connector.Connector) keyboardController._ps2;
        ps2Connector.setClkSocket(new RtlBitConstant(realm, true));
        ps2Connector.setDataSocket(new RtlBitConstant(realm, true));

        //
        // signal logger
        //
        loggerInterface = (SignalLoggerBusInterface.Connector) computerModule._signalLogger;
        signalLogger = new SignalLogger.Implementation(realm, design.getClock(), design.getClock());
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

        // SPI (output only; unused for now)
        {
            SpiInterface.Implementation spiInterface = (SpiInterface.Implementation) design.getComputerModule()._spiInterface;
            SpiConnector.Connector connector = (SpiConnector.Connector) spiInterface._spiConnector;
        }

        // set debugger breakpoints here to allow clock stepping
        new RtlClockedSimulationItem(design.getClock()) {

            @Override
            public void computeNextState() {
            }

            @Override
            public void updateState() {
            }

        };

        // simulate
        new RtlClockGenerator(design.getClock(), 10);

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

    public static void main(String[] args) throws Exception {
        CeeCompilerInvoker.invoke();
        SimulationMain main = new SimulationMain();
        main.design.simulate();
        if (main.openglHelper != null) {
            main.openglHelper.destroy();
        }
    }

    public int readFromSimulationDevice(int wordAddress) {
        int majorAddress = wordAddress >> 16;
        if (majorAddress == 1) {
            return openglHelper == null ? 0 : openglHelper.read(wordAddress);
        }
        switch (wordAddress) {

            case 0:
                // return 1 to show that this is a simulation
                return 1;

            default:
                return 0;

        }
    }

    public void writeToSimulationDevice(int wordAddress, int byteMask, int data) {
        int majorAddress = wordAddress >> 16;
        if (majorAddress == 1) {
            if (openglHelper != null) {
                openglHelper.write(wordAddress, byteMask, data);
            }
            return;
        }
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

    private void debugPrint(int subcode) {
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

    private void memoryHelper(int subcode) {
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

    public byte readMemoryByte(int address) {
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

    public byte[] readMemoryBytes(int startAddress, int count) {
        byte[] result = new byte[count];
        for (int i = 0; i < count; i++) {
            result[i] = readMemoryByte(startAddress + i);
        }
        return result;
    }

    public String readMemoryString(int startAddress, int count) {
        return new String(readMemoryBytes(startAddress, count), StandardCharsets.ISO_8859_1);
    }

    public String readZeroTerminatedMemoryString(int startAddress) {
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

}
