/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.lattice;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.pin.RtlPinConfiguration;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class ProjectGenerator {

	private final RtlRealm realm;
	private final String name;
	private final File outputFolder;
	private final String fpgaPartId;
	private final List<String> additionalPcfLines = new ArrayList<>();
	private final List<File> additionalVerilogFiles = new ArrayList<>();

	public ProjectGenerator(RtlRealm realm, String name, File outputFolder, String fpgaPartId) {
		this.realm = realm;
		this.name = name;
		this.outputFolder = outputFolder;
		this.fpgaPartId = fpgaPartId;
	}

	public void addPcfLine(String line) {
		additionalPcfLines.add(line);
	}

	public void addVerilogFile(File path) {
		additionalVerilogFiles.add(path);
	}

	public void generate() throws IOException {
		outputFolder.mkdirs();

		for (File file : additionalVerilogFiles) {
			FileUtils.copyFileToDirectory(file, outputFolder);
		}

		AuxiliaryFileFactory auxiliaryFileFactory =
			filename -> new FileOutputStream(new File(outputFolder, filename));

		generateFile(name + ".v", out -> {
			VerilogGenerator verilogGenerator = new VerilogGenerator(out, realm, name, auxiliaryFileFactory);
			verilogGenerator.generate();
		});

		generateFile("build.pcf", out -> {
			for (RtlPin pin : realm.getPins()) {
				RtlPinConfiguration pinConfiguration = pin.getConfiguration();
				if (!(pinConfiguration instanceof LatticePinConfiguration)) {
					throw new RuntimeException("cannot process pin configuration (not a LatticePinConfiguration): " + pinConfiguration);
				}
				LatticePinConfiguration latticePinConfiguration = (LatticePinConfiguration) pinConfiguration;
				latticePinConfiguration.writePcf(pin, out);
			}
			for (String additionalPcfLine : additionalPcfLines) {
				out.println(additionalPcfLine);
			}
		});

	}

	private void generateFile(String filename, Consumer<PrintWriter> contentGenerator) throws IOException {
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
