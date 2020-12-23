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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class ProjectGenerator {

	public static final boolean SHOW_OUTPUT = false;

	private static final File toolFolder = new File("/home/martin.geisse/tools/fpga-toolchain/bin");

	private final RtlRealm realm;
	private final String name;
	private final File outputFolder;
	private final String fpgaPartId;
	private final List<String> additionalPcfLines = new ArrayList<>();
	private final List<File> additionalVerilogFiles = new ArrayList<>();
	private Integer nextBootAddress;

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

	public Integer getNextBootAddress() {
		return nextBootAddress;
	}

	public void setNextBootAddress(Integer nextBootAddress) {
		this.nextBootAddress = nextBootAddress;
	}

	public void clean() throws IOException {
		FileUtils.deleteDirectory(outputFolder);
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

		generateFile("design.pcf", out -> {
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

	public void build() throws IOException, InterruptedException {
		execTool("design.json", "synthesis", "yosys", "-p", "synth_ecp5 -json design.json", name + ".v");
		execTool("design.config", "pnr", "nextpnr-ecp5", "--json", "design.json", "--textcfg", "design.config",
				"--25k", "--package", fpgaPartId, "--lpf", "design.pcf");
		execTool("design.bit", "ecppack", "ecppack", "--compress", "--freq", "38.8", "--input", "design.config",
				"--bit", "design.bit",
				nextBootAddress != null ? "--bootaddr" : null,
				nextBootAddress != null ? ("0x" + Integer.toHexString(nextBootAddress)) : null);
		if (new File(outputFolder, "design.bit").length() > 512 * 1024) {
			System.err.println("design.bit is too large (max size: 4Mb == 512kB");
			System.exit(1);
		}
		FileUtils.copyFile(new File(outputFolder, "design.bit"), new File(outputFolder, "design.dfu"));
		execTool("design.dfu", "dfu-suffix", "dfu-suffix", "-v", "1209", "-p", "5af0", "-a", "design.dfu");
	}

	public void program() throws IOException, InterruptedException {
		execTool("design.dfu", "program", "dfu-util", "-D", "design.dfu");
	}

	private void execTool(String expectedOutputFilename, String logName, String... command) throws IOException, InterruptedException {
		command = removeNullElements(command);
		command[0] = new File(toolFolder, command[0]).getAbsolutePath();
		File expectedOutput = new File(outputFolder, expectedOutputFilename);
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(outputFolder);
		if (SHOW_OUTPUT) {
			builder.inheritIO();
		} else {
			builder.redirectOutput(new File(outputFolder, "log_out_" + logName + ".txt"));
			builder.redirectError(new File(outputFolder, "log_err_" + logName + ".txt"));
		}
		Process process = builder.start();
		int status = process.waitFor();
		if (status != 0 || !expectedOutput.exists()) {
			System.err.println();
			System.err.println("***********************************************");
			System.err.println("*** ERROR WHILE BUILDING FPGA CONFIGURATION ***");
			System.err.println("***********************************************");
			System.err.println();
			System.err.println("path: " + outputFolder);
			System.err.println("command: " + StringUtils.join(command, ' '));
			System.err.println("status code: " + status);
			System.err.println();
			System.err.flush();
			System.exit(1);
		}
		if (!SHOW_OUTPUT) {
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
		}
	}

	private static String[] removeNullElements(String[] array) {
		int resultCount = 0;
		for (String s : array) {
			if (s != null) {
				resultCount++;
			}
		}
		if (resultCount == array.length) {
			return array;
		}
		String[] result = new String[resultCount];
		int writePointer = 0;
		for (String s : array) {
			if (s != null) {
				result[writePointer] = s;
				writePointer++;
			}
		}
		return result;
	}

}
