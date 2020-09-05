package name.martingeisse.esdk.riscv.rtl.new_scope;

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
import name.martingeisse.esdk.riscv.rtl.CeeCompilerInvoker;
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
public class NewScopeSynthesisMain {

    public static void main(String[] args) throws Exception {
        CeeCompilerInvoker.invoke();
        NewScopeDesign design = new NewScopeDesign("riscv/resource/new-scope-program/build/program.bin") {
            @Override
            protected NewScope.Implementation createNewScope() {
                return new NewScope.Implementation(getRealm(), getClock(), getDdrClock0(), getDdrClock180(), getDdrClock270(), getDdrClock90()) {

                    @Override
                    protected RamController createBigRam(RtlRealm realm, RtlClockNetwork ddrClock0, RtlClockNetwork ddrClock180, RtlClockNetwork ddrClock270, RtlClockNetwork ddrClock90) {
                        return new RamController.Implementation(realm, ddrClock0, ddrClock180, ddrClock270, ddrClock90) {
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
        NewScope.Implementation newScope = design.getNewScope();
        RtlRealm realm = design.getRealm();

        // clk / reset
        RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset");
        RtlBitSignal reset = clkReset.createBitOutputPort("reset");
        clkReset.createBitInputPort("clk_in", clockPin(realm));
        clkReset.createBitInputPort("reset_in", buttonPin(realm, "V16"));
        newScope.setReset(reset);
        design.getDdrClock0SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_0"), "ddr_clk_0"));
        design.getDdrClock90SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_90"), "ddr_clk_90"));
        design.getDdrClock180SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_180"), "ddr_clk_180"));
        design.getDdrClock270SignalConnector().setConnected(withName(clkReset.createBitOutputPort("ddr_clk_270"), "ddr_clk_270"));
        design.getClockSignalConnector().setConnected(design.getDdrClock0SignalConnector().getConnected());

        // pixel display
        PixelDisplayController.Implementation displayController = (PixelDisplayController.Implementation) newScope._display;
        VgaConnector.Connector vgaConnector = (VgaConnector.Connector) displayController._vgaConnector;
        vgaPin(realm, "H14", vgaConnector.getRSocket());
        vgaPin(realm, "H15", vgaConnector.getGSocket());
        vgaPin(realm, "G15", vgaConnector.getBSocket());
        vgaPin(realm, "F15", vgaConnector.getHsyncSocket());
        vgaPin(realm, "F14", vgaConnector.getVsyncSocket());

        // keyboard
        KeyboardController.Implementation keyboardController = (KeyboardController.Implementation) newScope._keyboard;
        Ps2Connector.Connector ps2Connector = (Ps2Connector.Connector) keyboardController._ps2;
        ps2Connector.setClkSocket(ps2Pin(realm, "G14"));
        ps2Connector.setDataSocket(ps2Pin(realm, "G13"));

        // shared by signal logger
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

        //
        // signal logger
        //
        RtlVectorSignal logClockDivider = RegisterBuilder.build(8, VectorValue.of(8, 0),
                design.getClock(), r -> r.add(1));
        SignalLoggerBusInterface.Connector loggerInterface = (SignalLoggerBusInterface.Connector) newScope._signalLogger;
        SignalLogger signalLogger = new SignalLogger.Implementation(realm, design.getClock(), design.getClock());
        signalLogger.setLogEnable(logClockDivider.compareEqual(0));
        signalLogger.setLogData(RtlVectorConstant.of(realm, 28, 0).concat(buttonsAndSwitches.select(7, 4)));
        signalLogger.setBusEnable(loggerInterface.getBusEnableSocket());
        signalLogger.setBusWrite(loggerInterface.getBusWriteSocket());
        signalLogger.setBusWriteData(loggerInterface.getBusWriteDataSocket());
        loggerInterface.setBusReadDataSocket(signalLogger.getBusReadData());
        loggerInterface.setBusAcknowledgeSocket(signalLogger.getBusAcknowledge());

        //
        // GPIO (buttons and switches; LEDs not yet implemented)
        //
        newScope.setButtonsAndSwitches(buttonsAndSwitches);

        // SPI
        {
            SpiInterface.Implementation spiInterface = (SpiInterface.Implementation) design.getNewScope()._spiInterface;
            SpiConnector.Connector connector = (SpiConnector.Connector) spiInterface._spiConnector;

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

        ProjectGenerator projectGenerator = new ProjectGenerator(design.getRealm(), "NewScope", new File("ise/new-scope"), "XC3S500E-FG320-4");
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

    private static RtlInputPin inputPin(RtlRealm realm, String id, String ioStandard) {
        XilinxPinConfiguration configuration = new XilinxPinConfiguration();
        configuration.setIostandard(ioStandard);
        RtlInputPin pin = new RtlInputPin(realm);
        pin.setId(id);
        pin.setConfiguration(configuration);
        return pin;
    }

}
