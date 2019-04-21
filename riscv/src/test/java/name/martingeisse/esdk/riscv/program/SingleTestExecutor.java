package name.martingeisse.esdk.riscv.program;

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
				// I wanted to indicate binary numbers by a 0x prefix, but the official compliance tests use binary
				// numbers without a prefix. So now I indicate decimal numbers by an underscore prefix.
				if (line.startsWith("_")) {
					return (int)Long.parseLong(line.substring(1));
				} else {
					return (int)Long.parseLong(line, 16);
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
				throw new RuntimeException("program generated wrong result values (" + Integer.toHexString(value) + " should be " + Integer.toHexString(expectedValue.intValue()) + ")");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
