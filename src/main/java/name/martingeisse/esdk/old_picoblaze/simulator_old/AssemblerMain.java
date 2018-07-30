/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulator_old;

import name.martingeisse.esdk.old_picoblaze.assembler.IPicoblazeAssemblerErrorHandler;
import name.martingeisse.esdk.old_picoblaze.assembler.Range;
import name.martingeisse.esdk.old_picoblaze.assembler.ast.AstBuilder;
import name.martingeisse.esdk.old_picoblaze.assembler.ast.Context;
import name.martingeisse.esdk.old_picoblaze.assembler.ast.PsmFile;
import name.martingeisse.esdk.old_picoblaze.synthesis.PsmVerilogUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * TODO remove. This file was left as a code example for running the assembler.
 */
public class AssemblerMain {

	private static final IPicoblazeAssemblerErrorHandler errorHandler = new IPicoblazeAssemblerErrorHandler() {

		@Override
		public void handleWarning(final Range range, final String message) {
			handleError(range, message);
		}

		@Override
		public void handleError(final Range range, final String message) {
			throw new RuntimeException("ERROR at " + range + ": " + message);
		}

	};

	/**
	 * @param args ...
	 * @throws Exception ...
	 */
	public static void main(final String[] args) throws Exception {
		build("icpu_code.psm", "IcpuProgramMemory");
		build("ocpu_code.psm", "OcpuProgramMemory");
	}

	private static void build(String sourceName, String outputModuleName) throws Exception {
		final AstBuilder astBuilder = new AstBuilder();
		try (InputStreamReader reader = new InputStreamReader(AssemblerMain.class.getResourceAsStream(sourceName), StandardCharsets.UTF_8)) {
			astBuilder.parse(reader, errorHandler);
		}
		PsmFile psmFile = astBuilder.getResult();
		Context context = new Context(errorHandler);
		psmFile.collectConstantsAndLabels(context);
		int[] encoded = psmFile.encode(context, errorHandler);
		FileUtils.write(new File(outputModuleName + ".v"), PsmVerilogUtil.generateMemoryVerilog(outputModuleName));
		FileUtils.write(new File(outputModuleName + ".mif"), PsmVerilogUtil.generateMif(encoded));
	}

}
