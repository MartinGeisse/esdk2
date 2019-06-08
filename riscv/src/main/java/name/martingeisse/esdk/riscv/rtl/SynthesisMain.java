package name.martingeisse.esdk.riscv.rtl;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.*;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.synthesis.prettify.RtlPrettifier;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.SignalLogger;
import name.martingeisse.esdk.library.SignalLoggerBusInterface;
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
		ComputerDesign design = new ComputerDesign();
		ComputerModule.Implementation computerModule = design.getComputerModule();
		RtlRealm realm = design.getRealm();

		RtlModuleInstance clkReset = new RtlModuleInstance(realm, "clk_reset");
		RtlBitSignal reset = clkReset.createBitOutputPort("reset");
		RtlBitSignal mainClockSignal = clkReset.createBitOutputPort("clk");
		clkReset.createBitInputPort("clk_in", clockPin(realm));
		clkReset.createBitInputPort("reset_in", buttonPin(realm, "V4"));
		design.getClockSignalConnector().setConnected(mainClockSignal);
		computerModule.setReset(reset);
		RtlBitSignal ddrClk0 = clkReset.createBitOutputPort("ddr_clk_0");
		RtlBitSignal ddrClk90 = clkReset.createBitOutputPort("ddr_clk_90");
		RtlBitSignal ddrClk180 = clkReset.createBitOutputPort("ddr_clk_180");
		RtlBitSignal ddrClk270 = clkReset.createBitOutputPort("ddr_clk_270");

		TextDisplayController.Implementation textDisplayController = (TextDisplayController.Implementation)computerModule._textDisplay;
		VgaConnector.Implementation vgaConnector = (VgaConnector.Implementation)textDisplayController._vgaConnector;
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
        // SDRAM
        //
        RtlModuleInstance ramController = new RtlModuleInstance(realm, "ddr_sdram");

        // generate DDR clock signal CK_P
		RtlModuleInstance sdramCkpDdr = new RtlModuleInstance(realm, "ODDR2");
		sdramCkpDdr.getParameters().put("DDR_ALIGNMENT", "NONE");
		sdramCkpDdr.getParameters().put("INIT", VectorValue.of(1, 0));
		sdramCkpDdr.getParameters().put("SRTYPE", "SYNC");
		sdramCkpDdr.setName("sdramCkpDdr");
		ramOutputPin(realm, "J5", sdramCkpDdr.createBitOutputPort("Q"));
		sdramCkpDdr.createBitInputPort("C0", ddrClk180);
		sdramCkpDdr.createBitInputPort("C1", ddrClk0);
		sdramCkpDdr.createBitInputPort("CE", new RtlBitConstant(realm, true));
		sdramCkpDdr.createBitInputPort("D0", new RtlBitConstant(realm, true));
		sdramCkpDdr.createBitInputPort("D1", new RtlBitConstant(realm, false));
		sdramCkpDdr.createBitInputPort("R", new RtlBitConstant(realm, false));
		sdramCkpDdr.createBitInputPort("S", new RtlBitConstant(realm, false));

		// generate DDR clock signal CK_N
		RtlModuleInstance sdramCknDdr = new RtlModuleInstance(realm, "ODDR2");
		sdramCknDdr.getParameters().put("DDR_ALIGNMENT", "NONE");
		sdramCknDdr.getParameters().put("INIT", VectorValue.of(1, 0));
		sdramCknDdr.getParameters().put("SRTYPE", "SYNC");
		sdramCknDdr.setName("sdramCknDdr");
		ramOutputPin(realm, "J4", sdramCknDdr.createBitOutputPort("Q"));
		sdramCknDdr.createBitInputPort("C0", ddrClk0);
		sdramCknDdr.createBitInputPort("C1", ddrClk180);
		sdramCknDdr.createBitInputPort("CE", new RtlBitConstant(realm, true));
		sdramCknDdr.createBitInputPort("D0", new RtlBitConstant(realm, true));
		sdramCknDdr.createBitInputPort("D1", new RtlBitConstant(realm, false));
		sdramCknDdr.createBitInputPort("R", new RtlBitConstant(realm, false));
		sdramCknDdr.createBitInputPort("S", new RtlBitConstant(realm, false));

		// system signals
		ramController.createBitInputPort("clk0", ddrClk0);
		ramController.createBitInputPort("clk90", ddrClk90);
		ramController.createBitInputPort("clk180", ddrClk180);
		ramController.createBitInputPort("clk270", ddrClk270);
		ramController.createBitInputPort("reset", reset);

		// SDRAM DDR interface
        ramOutputPin(realm, "K4", ramController.createBitOutputPort("sd_CS_O"));
        ramOutputPin(realm, "K3", ramController.createBitOutputPort("sd_CKE_O"));
        ramOutputPin(realm, "C1", ramController.createBitOutputPort("sd_RAS_O"));
        ramOutputPin(realm, "C2", ramController.createBitOutputPort("sd_CAS_O"));
        ramOutputPin(realm, "D1", ramController.createBitOutputPort("sd_WE_O"));
        ramOutputPin(realm, "J1", ramController.createBitOutputPort("sd_UDM_O"));
        ramOutputPin(realm, "J2", ramController.createBitOutputPort("sd_LDM_O"));
        ramOutputPinArray(realm, ramController.createVectorOutputPort("sd_A_O", 13),
                "T1", "R3", "R2", "P1", "F4", "H4", "H3", "H1", "H2", "N4", "T2", "N5", "P2");
        ramOutputPinArray(realm, ramController.createVectorOutputPort("sd_BA_O", 2), "K5", "K6");
        ramBidirectionalPin(realm, "G3", ramController, "sd_UDQS_IO");
        ramBidirectionalPin(realm, "L6", ramController, "sd_LDQS_IO");
		RtlBidirectionalModulePortPinArray ramDataPinArray = new RtlBidirectionalModulePortPinArray(
			realm, ramController, "sd_D_IO", "sd_D_IO",
				"L2", "L1", "L3", "L4", "M3", "M4", "M5", "M6", "E2", "E1", "F1", "F2", "G6", "G5", "H6", "H5"
		);
		for (RtlPin pin : ramDataPinArray.getPins()) {
			XilinxPinConfiguration configuration = new XilinxPinConfiguration();
			configuration.setIostandard("SSTL2_I");
			pin.setConfiguration(configuration);
		}

        // internal Wishbone interface
        ramController.createBitInputPort("wSTB_I", computerModule._bigRam.getEnable());
        ramController.createVectorInputPort("wADR_I", 24, computerModule._bigRam.getWordAddress());
        ramController.createBitInputPort("wWE_I", computerModule._bigRam.getWrite());
        ramController.createVectorInputPort("wDAT_I", 32, computerModule._bigRam.getWriteData());
        ramController.createVectorInputPort("wWRB_I", 4, computerModule._bigRam.getWriteMask());
        computerModule._bigRam.getReadData().setConnected(ramController.createVectorOutputPort("wDAT_O", 32));
        computerModule._bigRam.getAcknowledge().setConnected(ramController.createBitOutputPort("wACK_O"));

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
		projectGenerator.addVerilogFile(new File("riscv/resource/hdl/ddr_sdram.v"));
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

	private static RtlInputPin ramInputPin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("SSTL2_I");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlOutputPin ramOutputPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("SSTL2_I");
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlBidirectionalModulePortPin ramBidirectionalPin(RtlRealm realm, String pinId, RtlModuleInstance moduleInstance, String portName) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("SSTL2_I");
        RtlBidirectionalModulePortPin pin = new RtlBidirectionalModulePortPin(realm, moduleInstance, portName);
		pin.setId(pinId);
		pin.setConfiguration(configuration);
		return pin;
	}

	private static void ramOutputPinArray(RtlRealm realm, RtlVectorSignal outputSignal, String... ids) {
		if (ids.length != outputSignal.getWidth()) {
			throw new IllegalArgumentException("vector width (" + outputSignal.getWidth() +
					") does not match number of pin IDs (" + ids.length + ")");
		}
		for (int i = 0; i < outputSignal.getWidth(); i++) {
			RtlBitSignal bitSignal = new RtlConstantIndexSelection(realm, outputSignal, i);
			ramOutputPin(realm, ids[i], bitSignal);
		}
	}

}
