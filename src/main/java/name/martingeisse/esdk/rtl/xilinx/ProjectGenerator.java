/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.xilinx;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlPin;
import name.martingeisse.esdk.rtl.RtlPinConfiguration;
import name.martingeisse.esdk.rtl.VerilogDesignGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class ProjectGenerator {

	private final RtlDesign design;
	private final String name;
	private final File outputFolder;
	private final String fpgaPartId;
	private final List<String> additionalUcfLines = new ArrayList<>();

	public ProjectGenerator(RtlDesign design, String name, File outputFolder, String fpgaPartId) {
		this.design = design;
		this.name = name;
		this.outputFolder = outputFolder;
		this.fpgaPartId = fpgaPartId;
	}

	public void addUcfLine(String line) {
		additionalUcfLines.add(line);
	}

	public void generate() throws IOException {
		outputFolder.mkdirs();

		generateFile(name + ".v", out -> {
			new VerilogDesignGenerator(out, design, name).generate();
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
		});

		generateFile("build.sh", out -> {
			out.println("ssh martin@ise ./auto-ise/clean.sh");
			out.println("scp -r . martin@ise:./auto-ise/src");
			out.println("ssh martin@ise ./auto-ise/build.sh environment.sh");
		});

		generateFile("upload.sh", out -> {
			out.println("ssh martin@ise ./auto-ise/upload.sh");
		});

		generateFile("build.ucf", out -> {
			for (RtlPin pin : design.getPins()) {
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
