package name.martingeisse.esdk.library.riscv.program;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class SingleTestExecutor extends AbstractSingleTestExecutor {

	private final File resultFile;
	private LineNumberReader resultReader;

	public SingleTestExecutor(File textSegmentFile, File resultFile) {
		super(textSegmentFile);
		this.resultFile = resultFile;
	}

	public File getResultFile() {
		return resultFile;
	}

	public void execute() throws Exception {
		try (FileInputStream resultInputStream = new FileInputStream(resultFile)) {
			try (InputStreamReader rawResultReader = new InputStreamReader(resultInputStream, StandardCharsets.US_ASCII)) {
				try (LineNumberReader resultReader = new LineNumberReader(rawResultReader)) {
					this.resultReader = resultReader;
					super.execute();
					if (fetchNextExpectedResultValue() != null) {
						throw new RuntimeException("expected more result values");
					}
				} finally {
					this.resultReader = null;
				}
			}
		}
	}

	private Integer fetchNextExpectedResultValue() throws IOException {
		while (true) {
			String line = resultReader.readLine();
			if (line == null) {
				return null;
			}
			line = line.trim();
			if (!line.isEmpty()) {
				if (line.startsWith("0x")) {
					return Integer.parseInt(line.substring(2), 16);
				} else {
					return Integer.parseInt(line);
				}
			}
		}
	}

	@Override
	protected void handleOutputValue(int value) {
		try {
			Integer expectedValue = fetchNextExpectedResultValue();
			if (expectedValue == null) {
				throw new RuntimeException("program generated more result values than expected");
			} else if (expectedValue.intValue() != value) {
				throw new RuntimeException("program generated wrong result values");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
