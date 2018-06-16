package name.martingeisse.esdk.library.bus.bus32;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple bus using 32-bit address and data units.
 */
public class Bus32 extends Item {

	private BusMaster master;
	private final List<SlaveEntry> slaveEntries = new ArrayList<>();

	public Bus32(Design design) {
		super(design);
	}

	public BusMaster getMaster() {
		return master;
	}

	public void setMaster(BusMaster master) {
		this.master = master;
	}

	public List<SlaveEntry> getSlaveEntries() {
		return slaveEntries;
	}

	public void addSlave(SlaveEntry entry) {
		slaveEntries.add(entry);
	}

	public void addSlave(int address, int addressMask, BusSlave slave) {
		addSlave(new SlaveEntry(address, addressMask, slave));
	}

	public SlaveEntry getMatchingEntry(int address) {
		for (SlaveEntry slaveEntry : slaveEntries) {
			if (slaveEntry.matchesAddress(address)) {
				return slaveEntry;
			}
		}
		return null;
	}

	public void read(int address, ReadCallback callback) {
		// no delay for now
		// for now, reads 0 if no matching slave was found
		SlaveEntry slaveEntry = getMatchingEntry(address);
		if (slaveEntry == null) {
			fire(() -> callback.onReadFinished(0), 0);
		} else {
			slaveEntry.read(address, callback);
		}
	}

	public void write(int address, int data, WriteCallback callback) {
		// no delay for now
		// for now, does nothing (just calls the callback) if no matching slave was found
		SlaveEntry slaveEntry = getMatchingEntry(address);
		if (slaveEntry == null) {
			fire(callback::onWriteFinished, 0);
		} else {
			slaveEntry.write(address, data, callback);
		}
	}

}
