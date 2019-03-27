package name.martingeisse.esdk.library.riscv.program;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class StdoutSingleTestExecutor extends AbstractSingleTestExecutor {

	public StdoutSingleTestExecutor(File textSegmentFile) {
		super(textSegmentFile);
	}

	@Override
	protected void handleOutputValue(int value) {
		System.out.println(value + " / " + Integer.toHexString(value));
	}

}
