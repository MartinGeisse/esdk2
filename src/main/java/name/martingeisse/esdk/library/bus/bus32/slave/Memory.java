package name.martingeisse.esdk.library.bus.bus32.slave;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.library.bus.bus32.BusSlave;
import name.martingeisse.esdk.library.bus.bus32.ReadCallback;
import name.martingeisse.esdk.library.bus.bus32.WriteCallback;

/**
 * A simple on-chip memory that uses the lowest N address bits to store 2^N 32-bit words.
 */
public final class Memory extends Item implements BusSlave {

	private final int addressBits;
	private final int addressMask;
	private final int[] data;

	public Memory(Design design, int addressBits) {
		super(design);
		this.addressBits = addressBits;
		this.data = new int[1 << addressBits];
		this.addressMask = data.length - 1;
	}

	public int getAddressBits() {
		return addressBits;
	}

	public int getSize() {
		return data.length;
	}

	public int getValue(int address) {
		return data[address];
	}

	public void setValue(int address, int value) {
		data[address] = value;
	}

	@Override
	public void read(int address, ReadCallback callback) {
		// no delay for now
		fire(() -> callback.onReadFinished(getValue(address & addressMask)), 0);
	}

	@Override
	public void write(int address, int data, WriteCallback callback) {
		// no delay for now
		fire(() -> {
			setValue(address & addressMask, data);
			callback.onWriteFinished();
		}, 0);
	}

}
