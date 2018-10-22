package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 *
 */
public class RamTestControllerSynthesisMain {

	private static final File outputFolder = new File("ise/ramtest");

	public static void main(String[] args) throws Exception {

		// build design
		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		// TODO

		// generate output file
		outputFolder.mkdirs();
		AuxiliaryFileFactory auxiliaryFileFactory =
			filename -> new FileOutputStream(new File(outputFolder, filename));
		generateFile("RamTestController.v", out -> {
			new VerilogGenerator(out, realm, "RamTestController", auxiliaryFileFactory).generate();
		});

	}

	private static void generateFile(String filename, Consumer<PrintWriter> contentGenerator) throws IOException {
		File file = new File(outputFolder, filename);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.US_ASCII)) {
				try (PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
					contentGenerator.accept(printWriter);
				}
			}
		}
	}

}
