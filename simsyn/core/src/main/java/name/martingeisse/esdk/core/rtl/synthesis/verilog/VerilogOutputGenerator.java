/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.verilog;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Generates a folder with a verilog file as the final output to use in other projects.
 */
public class VerilogOutputGenerator {

	private final RtlRealm realm;
	private final String name;
	private final File outputFolder;

	public VerilogOutputGenerator(RtlRealm realm, String name, File outputFolder) {
		this.realm = realm;
		this.name = name;
		this.outputFolder = outputFolder;
	}

	public void clean() throws IOException {
		FileUtils.deleteDirectory(outputFolder);
	}

	public void generate() throws IOException {
		outputFolder.mkdirs();

		AuxiliaryFileFactory auxiliaryFileFactory =
			filename -> new FileOutputStream(new File(outputFolder, filename));

		generateFile(name + ".v", out -> {
			VerilogGenerator verilogGenerator = new VerilogGenerator(out, realm, name, auxiliaryFileFactory);
			verilogGenerator.generate();
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
