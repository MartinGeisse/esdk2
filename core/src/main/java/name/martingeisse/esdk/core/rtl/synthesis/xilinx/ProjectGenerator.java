/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.synthesis.xilinx;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.pin.RtlPinConfiguration;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogNames;
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
	private final List<String> additionalUcfLines = new ArrayList<>();
	private final List<File> additionalVerilogFiles = new ArrayList<>();
	private VerilogNames verilogNames;

	public ProjectGenerator(RtlRealm realm, String name, File outputFolder, String fpgaPartId) {
		this.realm = realm;
		this.name = name;
		this.outputFolder = outputFolder;
		this.fpgaPartId = fpgaPartId;
	}

	public void addUcfLine(String line) {
		additionalUcfLines.add(line);
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
			verilogNames = verilogGenerator.getNames();
		});

		generateFile("environment.sh", out -> {
			out.println("export XST_SCRIPT_FILE=build.xst");
			out.println("export CONSTRAINTS_FILE=build.ucf");
		});

		generateFile("build.xst", out -> {
			out.println("set -tmpdir build/xst_temp");
			out.println("run");
			out.println("-ifn src/build.prj");
			out.println("-ofmt NGC");
			out.println("-ofn build/synthesized.ngc");
			out.println("-top " + name);
			out.println("-p " + fpgaPartId);
			out.println("-opt_level 1");
			out.println("-opt_mode SPEED");
		});

		generateFile("build.prj", out -> {
			out.println("verilog work " + name + ".v");
			for (File file : additionalVerilogFiles) {
				out.println("verilog work " + file.getName());
			}
		});

		generateFile("build.sh", out -> {
			out.println("ssh martin@ise ./auto-ise/clean.sh");
			out.println("scp -r . martin@ise:./auto-ise/src");
			out.println("ssh martin@ise ./auto-ise/build.sh environment.sh");
		});

		generateFile("upload.sh", out -> {
			out.println("ssh martin@ise ./auto-ise/upload.sh");
		});

		generateFile("upload-prom.sh", out -> {
			out.println("ssh martin@ise ./auto-ise/upload-prom.sh");
		});

		generateFile("build.ucf", out -> {
			for (RtlPin pin : realm.getPins()) {
				RtlPinConfiguration pinConfiguration = pin.getConfiguration();
				if (!(pinConfiguration instanceof XilinxPinConfiguration)) {
					throw new RuntimeException("cannot process pin configuration (not a XilinxPinConfiguration): " + pinConfiguration);
				}
				XilinxPinConfiguration xilinxPinConfiguration = (XilinxPinConfiguration) pinConfiguration;
				xilinxPinConfiguration.writeUcf(pin, out);
			}
			for (String additionalUcfLine : additionalUcfLines) {
				out.println(additionalUcfLine);
			}
			for (RtlItem item : realm.getItems()) {
				if (item instanceof UcfContributor) {
					((UcfContributor) item).contributeToUcf(out, verilogNames);
				}
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
