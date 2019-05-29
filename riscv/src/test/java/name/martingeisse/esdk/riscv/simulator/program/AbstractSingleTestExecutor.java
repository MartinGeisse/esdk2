package name.martingeisse.esdk.riscv.simulator.program;

import name.martingeisse.esdk.riscv.instruction.InstructionLevelRiscv;
import name.martingeisse.esdk.riscv.instruction.io.IoUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public abstract class AbstractSingleTestExecutor {

	private final File textSegmentFile;
	private final int[] memory;
	private final MyCpu cpu;

	private boolean stopped;

	public AbstractSingleTestExecutor(File textSegmentFile) {
		this.textSegmentFile = textSegmentFile;
		this.memory = new int[1024 * 1024];
		this.cpu = new MyCpu();
		this.stopped = false;
	}

	public File getTextSegmentFile() {
		return textSegmentFile;
	}

	public int[] getMemory() {
		return memory;
	}

	public void execute() throws Exception {
		loadProgram();
		while (!stopped) {
			cpu.step();
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

	protected abstract void handleOutputValue(int value);

	private class MyCpu extends InstructionLevelRiscv {

		public MyCpu() {
			setIoUnit(new IoUnit() {

				@Override
				public int fetchInstruction(int wordAddress) {
					if (wordAddress < 0 || wordAddress >= memory.length) {
						throw new IllegalArgumentException("illegal address for instruction fetch: " + wordAddress);
					}
					return memory[wordAddress];
				}

				@Override
				public int read(int wordAddress) {
					if (wordAddress < 0 || wordAddress >= memory.length) {
						throw new IllegalArgumentException("illegal address for reading: " + wordAddress);
					}
					return memory[wordAddress];
				}

				@Override
				public void write(int wordAddress, int data, int byteMask) {
					if (byteMask != (byteMask & 15)) {
						throw new IllegalArgumentException("illegal byte mask for writing: " + wordAddress);
					}
					if (wordAddress == -8) {
						handleOutputValue(data);
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

			});
		}

		private void writeHelper(int wordAddress, int data, int selectedByteMask, int bitMask) {
			if (selectedByteMask != 0) {
				memory[wordAddress] = (memory[wordAddress] & ~bitMask) | (data & bitMask);
			}
		}

	}
}
