package name.martingeisse.esdk.riscv.rtl;

import java.io.File;

/**
 *
 */
public class StdoutProgramTestMain {

	private static final String TEXT_SEGMENT_FILENAME = "MUL.bin";

	public static void main(String[] args) throws Exception {
		File buildFolder = new File("core/resource/riscv-test/build");
		File textSegmentFile = new File(buildFolder, TEXT_SEGMENT_FILENAME);
		new StdoutSingleTestExecutor(textSegmentFile).execute();
	}

}
