package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;

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
		RamTestController controller = new RamTestController();

		// generate output file
		if (!outputFolder.mkdirs()) {
			throw new RuntimeException();
		}
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

}
