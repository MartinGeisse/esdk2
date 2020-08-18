package name.martingeisse.esdk.riscv.rtl;

import java.io.File;

/**
 *
 */
public class ProgramTestMain {

	public static void main(String[] args) throws Exception {
		File buildFolder = new File("core/resource/riscv-test/build");
		for (File resultFile : buildFolder.listFiles((ignored, name) -> name.endsWith(".txt"))) {
			try {
				String resultFileName = resultFile.getName();
				String textSegmentFileName = resultFileName.substring(0, resultFileName.length() - 4) + ".bin";
				System.out.println("testing " + textSegmentFileName + " against " + resultFileName);
				File textSegmentFile = new File(resultFile.getParent(), textSegmentFileName);
				new SingleTestExecutor(textSegmentFile, resultFile).execute();
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
			System.out.println();
		}
	}

}
