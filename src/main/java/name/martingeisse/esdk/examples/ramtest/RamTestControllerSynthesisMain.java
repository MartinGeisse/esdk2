package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.validation.DesignValidationResult;
import name.martingeisse.esdk.core.model.validation.DesignValidator;
import name.martingeisse.esdk.core.model.validation.print.WriterValidationResultPrinter;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;
import name.martingeisse.esdk.library.bus.wishbone.WishboneSimpleMaster;

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
		RtlClockNetwork clock = realm.createClockNetwork(inPin(realm, "clock"));
		RamTestController controller = new RamTestController(realm, clock);
		RtlVectorSignal leds = controller.getLeds();

		// add pins
		outPin(realm, "led0", leds.select(0));
		outPin(realm, "led1", leds.select(1));
		outPin(realm, "led2", leds.select(2));
		outPin(realm, "led3", leds.select(3));
		outPin(realm, "led4", leds.select(4));
		outPin(realm, "led5", leds.select(5));
		outPin(realm, "led6", leds.select(6));
		outPin(realm, "led7", leds.select(7));
		WishboneSimpleMaster wishboneMaster = controller.getWishboneMaster();
		outPin(realm, "wbCycleStrobe", wishboneMaster.getCycleStrobeSignal());
		TODO

		// validate
		DesignValidationResult validationResult = new DesignValidator(design).validate();
		WriterValidationResultPrinter printer = new WriterValidationResultPrinter(System.out);
		validationResult.format(printer);
		printer.flush();

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
		return pin;
	}

	private static RtlInputPin inPin(RtlRealm realm, String id) {
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		return pin;
	}

}
