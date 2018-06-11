package name.martingeisse.esdk.library.bus.bus32.slave;

/**
 * A simple on-chip memory that uses the lowest N address bits to store 2^N 32-bit words.
 */
public final class Memory {

	private final int addressBits;
	private final int addressMask;
	private final int[] memory;

	public Memory(int addressBits) {
		this.addressBits = addressBits;
		this.memory = new int[1 << addressBits];
		this.addressMask = memory.length - 1;
	}

	public int getAddressBits() {
		return addressBits;
	}

	public int getSize() {
		return memory.length;
	}

	public int getValue(int address) {
		return memory[address];
	}

	public int setValue(int address, int value) {
		memory[address] = value;
	}

	// TODO is this object the static model or the run-time simulation state?

}
