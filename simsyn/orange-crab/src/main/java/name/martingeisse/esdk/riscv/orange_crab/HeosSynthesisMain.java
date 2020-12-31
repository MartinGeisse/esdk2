package name.martingeisse.esdk.riscv.orange_crab;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlBidirectionalPin;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.LatticePinConfiguration;
import name.martingeisse.esdk.core.rtl.synthesis.lattice.ProjectGenerator;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.util.RegisterBuilder;
import name.martingeisse.esdk.riscv.orange_crab.ddr3.RamController;
import name.martingeisse.esdk.riscv.orange_crab.ddr3.SdramConnector;
import name.martingeisse.esdk.riscv.rtl.CeeCompilerInvoker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class HeosSynthesisMain {

	public static void main(String[] args) throws Exception {

		CeeCompilerInvoker.invoke("orange-crab/resource/heos");

		// create design and realm
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);

		// create clock network
		RtlModuleInstance pll = new RtlModuleInstance(realm, "EHXPLLL");
		// ports
		pll.createBitInputPort("CLKI", clockPin(realm));
//		pll.createBitInputPort("CLKI2", false);
//		pll.createBitInputPort("SEL", false);
//		pll.createBitInputPort("CLKFB", false);
//		pll.createVectorInputPort("PHASESEL", 2, RtlVectorConstant.of(realm, 2, 0));
//		pll.createBitInputPort("PHASEDIR", false);
//		pll.createBitInputPort("PHASESTEP", false);
//		pll.createBitInputPort("PHASELOADREG", false);
		pll.createBitInputPort("STDBY", false);
		pll.createBitInputPort("RST", false);
		pll.createBitInputPort("ENCLKOP", true);
//		pll.createBitInputPort("ENCLKOS", false);
//		pll.createBitInputPort("ENCLKOS2", false);
		pll.createBitInputPort("ENCLKOS3", true); // not clear if this is needed when using S3 as feedback, probably not
		RtlClockNetwork clock = realm.createClockNetwork(pll.createBitOutputPort("CLKOP"));
		// "make it work" attributes and parameters
		pll.getAttributes().put("FREQUENCY_PIN_CLKI", 48);
		// The following settings are seemingly used by foboot, but for some reason cause extremely long startup times.
		// Since the attributes are not documented, nobody knows why.
//		pll.getAttributes().put("ICP_CURRENT", "6");
//		pll.getAttributes().put("LPF_RESISTOR", "16");
//		pll.getAttributes().put("MFG_ENABLE_FILTEROPAMP", "1");
//		pll.getAttributes().put("MFG_GMCREF_SEL", "2");
		// input clock
		pll.getParameters().put("CLKI_DIV", 2);
		// output clock
		pll.getParameters().put("CLKOP_ENABLE", "ENABLED");
		pll.getParameters().put("CLKOP_DIV", 12);
		pll.getParameters().put("CLKOP_FPHASE", 0);
		pll.getParameters().put("CLKOP_CPHASE", 0);
		// feedback clock
		pll.getParameters().put("FEEDBK_PATH", "INT_OS3");
		pll.getParameters().put("CLKOS3_ENABLE", "ENABLED");
		pll.getParameters().put("CLKOS3_DIV", 1);
		pll.getParameters().put("CLKOS3_FPHASE", 0);
		pll.getParameters().put("CLKOS3_CPHASE", 0);
		pll.getParameters().put("CLKFB_DIV", 25);

		// create main RTL
		SdramConnector.Connector sdramConnector = new SdramConnector.Connector(realm);
		Heos.Implementation implementation = new Heos.Implementation(realm, clock, clock) {
			@Override
			protected RamController createRamController(RtlRealm realm, RtlClockNetwork clk) {
				return new RamController.Implementation(realm, clk) {
					@Override
					protected SdramConnector createSdram(RtlRealm realm) {
						return sdramConnector;
					}
				};
			}
		};

		// LED pins
		outputPin(realm, "K4", "LVCMOS33", null, implementation.getLedRn());
		outputPin(realm, "M3", "LVCMOS33", null, implementation.getLedGn());
		outputPin(realm, "J3", "LVCMOS33", null, implementation.getLedBn());

		// VGA pins
		outputPin(realm, "J2", "LVCMOS33", null, implementation.getR().select(2)); // IO 13
		outputPin(realm, "H2", "LVCMOS33", null, implementation.getR().select(1)); // IO 12
		outputPin(realm, "A8", "LVCMOS33", null, implementation.getR().select(0)); // IO 11
		outputPin(realm, "B8", "LVCMOS33", null, implementation.getG().select(2)); // IO 10
		outputPin(realm, "C8", "LVCMOS33", null, implementation.getG().select(1)); // IO 9
		outputPin(realm, "B9", "LVCMOS33", null, implementation.getG().select(0)); // IO 6
		outputPin(realm, "B10", "LVCMOS33", null, implementation.getB().select(2)); // IO 5
		outputPin(realm, "C9", "LVCMOS33", null, implementation.getB().select(1)); // SCL
		outputPin(realm, "C10", "LVCMOS33", null, implementation.getB().select(0)); // SDA
		outputPin(realm, "N16", "LVCMOS33", null, implementation.getHsync()); // MOSI
		outputPin(realm, "N15", "LVCMOS33", null, implementation.getVsync()); // MISO

		// PS/2 pins
		implementation.setPs2clk(withPullup(inputPin(realm, "N17", "LVCMOS33"))); // IO 0
		implementation.setPs2data(withPullup(inputPin(realm, "M18", "LVCMOS33"))); // IO 1

		// use the pushbutton to reconfigure the FPGA
		RtlBitSignal button = inputPin(realm, "J17", "SSTL135_I");
		inOutPin(realm, "V17", "LVCMOS33", null, new RtlBitConstant(realm, false), button.not());

		// SDRAM pins
		//
		// It seems that the implementation process recognizes the differential pin-pairs as such and only expects the
		// positive pin to be declared in the constraints file. This is probably triggered by the extra "D" in the
		// IO type.
		//
		// The "_I" and "_II" suffixes seem to be different kinds of termination.
		//
		// Unfortunately, documentation on this is hard to find, and lattice documentation in particular gets
		// mis-indexed by google on a regular basis.
		//
		// Also, I skipped the VCCIO and GND pins for now. These are listed in the original constraints file but not
		// actually used anywhere in the design. Externally they are hardwired to the supply voltage and GND, so I
		// don't get at all what to use them for, or why they are even connected to the FPGA. Possibly they can be
		// used to check if the supply voltage is stable already.
		// Comment by Greg about these pins:
		// 		Lattice refers to these as "Virtual" power pins.
		//		I believe the logic is to attach un-used I/O pins to VCCIO/GND, and then drive them in gateware to
		//			offer more return paths for signals to aid in reducing switching noise.
		// 		By default these pins are tri-stated. So if you're not using the DRAM in your design it's okay to leave
		// 			them out of the pin constraints.
		//
		{
			// control
			outputPin(realm, "J18", "SSTL135D_I", "FAST", sdramConnector.getCKSocket());

			outputPin(realm, "D18", "SSTL135_I", "FAST", sdramConnector.getCKESocket());
			outputPin(realm, "C13", "SSTL135_I", "FAST", sdramConnector.getODTSocket());
			outputPin(realm, "L18", "SSTL135_I", "FAST", sdramConnector.getRESETnSocket());

			outputPin(realm, "C12", "SSTL135_I", "FAST", sdramConnector.getRASnSocket());
			outputPin(realm, "D13", "SSTL135_I", "FAST", sdramConnector.getCASnSocket());
			outputPin(realm, "B12", "SSTL135_I", "FAST", sdramConnector.getWEnSocket());
			outputPin(realm, "A12", "SSTL135_I", "FAST", sdramConnector.getCSnSocket());
			outputPin(realm, "D16", "SSTL135_I", "FAST", sdramConnector.getDataOutMaskSocket().select(0));
			outputPin(realm, "G16", "SSTL135_I", "FAST", sdramConnector.getDataOutMaskSocket().select(1));
		}
		{
			// address
			String[] addressPinIds = {"C4", "D2", "D3", "A3", "A4", "D4", "C3", "B2",
				"B1", "D1", "A7", "C2", "B6", "C1", "A2", "C7"};
			for (int i = 0; i < 16; i++) {
				outputPin(realm, addressPinIds[i], "SSTL135_I", "FAST", sdramConnector.getASocket().select(i));
			}
		}
		{
			// bank address
			String[] bankAddressPinIds = {"D6", "B7", "A6"};
			for (int i = 0; i < 3; i++) {
				outputPin(realm, bankAddressPinIds[i], "SSTL135_I", "FAST", sdramConnector.getBASocket().select(i));
			}
		}
		{
			// data
			RtlVectorSignal dataIn = new RtlVectorConstant(realm, VectorValue.of(0, 0));
			String[] dataPinIds = {"C17", "D15", "B17", "C16", "A15", "B13", "A17", "A13",
				"F17", "F16", "G15", "F15", "J16", "C18", "H16", "F18"};
			for (int i = 0; i < 16; i++) {
				RtlBidirectionalPin pin = inOutPin(realm, dataPinIds[i], "SSTL135_I", "FAST",
						sdramConnector.getDataOutSocket().select(i), sdramConnector.getDriveDataSocket());
				((LatticePinConfiguration)pin.getConfiguration()).set("TERMINATION", "OFF");
				dataIn = pin.concat(dataIn);
			}
			sdramConnector.setDataInSocket(dataIn);
		}
		RtlBidirectionalPin udqs, ldqs;
		{
			// data strobe
			udqs = inOutPin(realm, "B15", "SSTL135D_I", "FAST",
					sdramConnector.getDataStrobeOutSocket(),
					sdramConnector.getDriveDataStrobeSocket());
			((LatticePinConfiguration) udqs.getConfiguration()).set("TERMINATION", "OFF");
			((LatticePinConfiguration) udqs.getConfiguration()).set("DIFFRESISTOR", "100");

			ldqs = inOutPin(realm, "G18", "SSTL135D_I", "FAST",
					sdramConnector.getDataStrobeOutSocket(),
					sdramConnector.getDriveDataStrobeSocket());
			((LatticePinConfiguration) ldqs.getConfiguration()).set("TERMINATION", "OFF");
			((LatticePinConfiguration) ldqs.getConfiguration()).set("DIFFRESISTOR", "100");
		}


		// load the program into small memory
		try (FileInputStream in = new FileInputStream("orange-crab/resource/heos/build/program.bin")) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				implementation._memory0.getMatrix().setRow(index, VectorValue.of(8, first));
				implementation._memory1.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				implementation._memory2.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				implementation._memory3.getMatrix().setRow(index, VectorValue.of(8, readByteEofSafe(in)));
				index++;
			}
		}

		// signal logging
//		RtlVectorSignal dummyCounter = RegisterBuilder.build(32, VectorValue.of(32, 0), clock, r -> r.add(1));
//		// RtlVectorSignal dummyShifter = RegisterBuilder.build(8, VectorValue.of(8, 1), clock, r -> r.select(0).concat(r.select(7, 1)));
//		implementation.setSignalLogEnable(new RtlBitConstant(realm, true));
//		implementation.setSignalLogData(dummyCounter);
//		// implementation.setSignalLogData(new RtlVectorConstant(realm, VectorValue.of(24, 0)).concat(dummyShifter));

		{
			RamController.Implementation ramController = ((RamController.Implementation) implementation._ramController);
			RtlBitSignal logStart = ramController._mainState.compareEqual(RamController.Implementation._STATE_WRITE_RAS);
			RtlBitSignal logEnable = RtlBuilder.bitRegister(clock, new RtlBitConstant(realm, true), logStart, false);
			// RtlBitSignal logEnable = new RtlBitConstant(realm, true);
			implementation.setSignalLogEnable(logEnable);
			implementation.setSignalLogData(new RtlConcatenation(realm,
					new RtlVectorConstant(realm, VectorValue.of(7, 0)),
					sdramConnector.getDataIn().select(7, 0),
					udqs,
					ldqs,
					sdramConnector.getRESETnSocket(),
					sdramConnector.getODTSocket(),
					sdramConnector.getDriveDataStrobeSocket(),
					sdramConnector.getDriveDataSocket(),
					sdramConnector.getWEnSocket(),
					sdramConnector.getCASnSocket(),
					sdramConnector.getRASnSocket(),
					sdramConnector.getCSnSocket(),
					sdramConnector.getCKESocket(),
					sdramConnector.getCKSocket(),
					ramController._mainState
			));
		}

		// generate Verilog and ISE project files
		ProjectGenerator projectGenerator = new ProjectGenerator(realm, "Heos", new File("synthesize/testbild"), "CSFBGA285");
		projectGenerator.clean();
		projectGenerator.generate();
		projectGenerator.build();
		projectGenerator.program();

	}

	private static <T extends Item> T withName(T item, String name) {
		item.setName(name);
		return item;
	}

	private static int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		RtlInputPin pin = inputPin(realm, "A9", "LVCMOS33");
		((LatticePinConfiguration)pin.getConfiguration()).setFrequency("48.0 MHz");
		return pin;
	}

	private static RtlInputPin inputPin(RtlRealm realm, String id, String ioType) {
		LatticePinConfiguration configuration = new LatticePinConfiguration();
		configuration.set("IO_TYPE", ioType);
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlOutputPin outputPin(RtlRealm realm, String id, String ioType, String slewRate, RtlBitSignal outputSignal) {
		LatticePinConfiguration configuration = new LatticePinConfiguration();
		configuration.set("IO_TYPE", ioType);
		if (slewRate != null) {
			configuration.set("SLEWRATE", slewRate);
		}
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlBidirectionalPin inOutPin(RtlRealm realm, String id, String ioType, String slewRate, RtlBitSignal outputSignal, RtlBitSignal enableSignal) {
		LatticePinConfiguration configuration = new LatticePinConfiguration();
		configuration.set("IO_TYPE", ioType);
		if (slewRate != null) {
			configuration.set("SLEWRATE", slewRate);
		}
		RtlBidirectionalPin pin = new RtlBidirectionalPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		pin.setOutputSignal(outputSignal);
		pin.setOutputEnableSignal(enableSignal);
		return pin;
	}

	private static RtlInputPin withPullup(RtlInputPin pin) {
		((LatticePinConfiguration)pin.getConfiguration()).set("PULLMODE", "UP");
		return pin;
	}

}
