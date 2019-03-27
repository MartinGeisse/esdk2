package name.martingeisse.esdk.library.riscv.program;

import name.martingeisse.esdk.library.riscv.InstructionLevelRiscv;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class SingleTestExecutor {

	private final File textSegmentFile;
	private final File resultFile;
	private final int[] memory;
	private final MyCpu cpu;

	private LineNumberReader resultReader;
	private boolean stopped;

	public SingleTestExecutor(File textSegmentFile, File resultFile) {
		this.textSegmentFile = textSegmentFile;
		this.resultFile = resultFile;
		this.memory = new int[1024 * 1024];
		this.cpu = new MyCpu();
		this.stopped = false;
	}

	public File getTextSegmentFile() {
		return textSegmentFile;
	}

	public File getResultFile() {
		return resultFile;
	}

	public void execute() throws Exception {
		loadProgram();
		try (FileInputStream resultInputStream = new FileInputStream(resultFile)) {
			try (InputStreamReader rawResultReader = new InputStreamReader(resultInputStream, StandardCharsets.US_ASCII)) {
				try (LineNumberReader resultReader = new LineNumberReader(rawResultReader)) {
					this.resultReader = resultReader;
					while (!stopped) {
						cpu.step();
					}
					if (fetchNextExpectedResultValue() != null) {
						throw new RuntimeException("expected more result values");
					}
				} finally {
					this.resultReader = null;
				}
			}
		}
	}

	private void loadProgram() throws Exception {
		try (FileInputStream in = new FileInputStream(textSegmentFile)) {
			int index = 0;
			while (true) {
				int first = in.read();
				if (first < 0) {
					break;
				}
				memory[index] = first | (readByteEofSafe(in) << 8) | (readByteEofSafe(in) << 16) | (readByteEofSafe(in) << 24);
				index++;
			}
		}
	}

	private int readByteEofSafe(InputStream in) throws IOException {
		int x = in.read();
		return (x < 0 ? 0 : x);
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

	private class MyCpu extends InstructionLevelRiscv {

		@Override
		protected int fetchInstruction(int wordAddress) {
			if (wordAddress < 0 || wordAddress >= memory.length) {
				throw new IllegalArgumentException("illegal address for instruction fetch: " + wordAddress);
			}
			return memory[wordAddress];
		}

		@Override
		protected int read(int wordAddress) {
			if (wordAddress < 0 || wordAddress >= memory.length) {
				throw new IllegalArgumentException("illegal address for reading: " + wordAddress);
			}
			return memory[wordAddress];
		}

		@Override
		protected void write(int wordAddress, int data, int byteMask) {
			if (byteMask != (byteMask & 15)) {
				throw new IllegalArgumentException("illegal byte mask for writing: " + wordAddress);
			}
			if (wordAddress == -8) {
				try {
					Integer expectedValue = fetchNextExpectedResultValue();
					if (expectedValue == null) {
						throw new RuntimeException("program generated more result values than expected");
					} else if (expectedValue.intValue() != data) {
						throw new RuntimeException("program generated wrong result values");
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return;
			}
			if (wordAddress == -9) {
				stopped = true;
				return;
			}
			if (wordAddress < 0 || wordAddress >= memory.length) {
				throw new IllegalArgumentException("illegal address for writing: " + wordAddress);
			}
			if (byteMask == 15) {
				memory[wordAddress] = data;
				return;
			}
			writeHelper(wordAddress, data, byteMask & 1, 0x000000ff);
			writeHelper(wordAddress, data, byteMask & 2, 0x0000ff00);
			writeHelper(wordAddress, data, byteMask & 4, 0x00ff0000);
			writeHelper(wordAddress, data, byteMask & 8, 0xff000000);
		}

		private void writeHelper(int wordAddress, int data, int selectedByteMask, int bitMask) {
			if (selectedByteMask != 0) {
				memory[wordAddress] = (memory[wordAddress] & ~bitMask) | (data & bitMask);
			}
		}

	}
}
