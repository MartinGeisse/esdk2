package name.martingeisse.esdk.riscv.instruction.program;

import java.io.*;

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
