package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.pin.simulation.RtlVectorInputPin;
import name.martingeisse.esdk.core.rtl.pin.simulation.RtlVectorOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;
import name.martingeisse.esdk.library.mybus.rtl.RtlMybusMaster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class RamTestControllerSynthesisMain {

	private static final File outputFolder = new File("ise/ramtest");

	public static void main(String[] args) throws Exception {

		// build design
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(inPin(realm, "Clock"));
		RamTestController controller = new RamTestController(realm, clock, inPin(realm, "Reset"));
		RtlVectorSignal leds = controller.getLeds();

		// add pins
		outPin(realm, "Leds", 8, leds);
		RtlMybusMaster mybusMaster = controller.getMybusMaster();
		outPin(realm, "MbCycleStrobe", mybusMaster.getStrobeSignal());
		outPin(realm, "MbWriteEnable", mybusMaster.getWriteEnableSignal());
		outPin(realm, "MbAddress", 32, mybusMaster.getAddressSignal());
		outPin(realm, "MbWriteData", 32, mybusMaster.getWriteDataSignal());
		mybusMaster.setReadDataSignal(inPin(realm, "MbReadData", 32));
		mybusMaster.setAckSignal(inPin(realm, "MbAck"));

		// validate
//		DesignValidationResult validationResult = new DesignValidator(design).validate();
//		WriterValidationResultPrinter printer = new WriterValidationResultPrinter(System.out);
//		validationResult.format(printer);
//		printer.flush();

		// generate output file
		outputFolder.mkdirs();
		AuxiliaryFileFactory auxiliaryFileFactory =
			filename -> new FileOutputStream(new File(outputFolder, filename));
		File file = new File(outputFolder, "RamTestController.v");
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.US_ASCII)) {
				try (PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
					new VerilogGenerator(printWriter, controller.getRealm(), "RamTestController", auxiliaryFileFactory).generate();
				}
			}
		}

	}

	private static RtlOutputPin outPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlVectorOutputPin outPin(RtlRealm realm, String id, int width, RtlVectorSignal outputSignal) {
		RtlVectorOutputPin pin = new RtlVectorOutputPin(realm, width);
		pin.setId(id);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlInputPin inPin(RtlRealm realm, String id) {
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		return pin;
	}

	private static RtlVectorInputPin inPin(RtlRealm realm, String id, int width) {
		RtlVectorInputPin pin = new RtlVectorInputPin(realm, width);
		pin.setId(id);
		return pin;
	}

}
