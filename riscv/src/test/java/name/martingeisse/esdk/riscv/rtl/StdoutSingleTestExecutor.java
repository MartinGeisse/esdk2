package name.martingeisse.esdk.riscv.rtl;

import java.io.File;

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
