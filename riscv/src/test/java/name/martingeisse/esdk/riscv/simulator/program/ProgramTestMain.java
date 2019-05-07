package name.martingeisse.esdk.riscv.simulator.program;

import java.io.File;

/**
 *
 */
public class ProgramTestMain {

	public static void main(String[] args) throws Exception {
		File buildFolder = new File("resource/riscv-test/build");
		for (File resultFile : buildFolder.listFiles((ignored, name) -> name.endsWith(".txt"))) {
			String resultFileName = resultFile.getName();
			String textSegmentFileName = resultFileName.substring(0, resultFileName.length() - 4) + ".bin";
			System.out.println("testing " + textSegmentFileName + " against " + resultFileName);
			File textSegmentFile = new File(resultFile.getParent(), textSegmentFileName);
			new SingleTestExecutor(textSegmentFile, resultFile).execute();
		}
	}

}
