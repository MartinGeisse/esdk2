package name.martingeisse.esdk.library.bus.bus32;

/**
 *
 */
public class SlaveEntry {

	private final int address;
	private final int addressMask;
	private final BusSlave slave;

	public SlaveEntry(int address, int addressMask, BusSlave slave) {
		this.address = address;
		this.addressMask = addressMask;
		this.slave = slave;
	}

	public int getAddress() {
		return address;
	}

	public int getAddressMask() {
		return addressMask;
	}

	public BusSlave getSlave() {
		return slave;
	}

}
