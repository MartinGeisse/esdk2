package name.martingeisse.esdk.library.mybus.transaction;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * No-delay transaction-level Mybus implementation.
 */
public class TransactionMybus extends Item {

	private final List<SlaveEntry> slaveEntries = new ArrayList<>();

	public TransactionMybus(Design design) {
		super(design);
	}

	public List<SlaveEntry> getSlaveEntries() {
		return slaveEntries;
	}

	public void addSlave(SlaveEntry entry) {
		slaveEntries.add(entry);
	}

	public void addSlave(int address, int addressMask, TransactionMybusSlave slave) {
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

	public int read(int address) {
		// no delay for now
		// for now, reads 0 if no matching slave was found
		SlaveEntry slaveEntry = getMatchingEntry(address);
		if (slaveEntry == null) {
			return 0;
		} else {
			return slaveEntry.read(address);
		}
	}

	public void write(int address, int data, int byteMask) {
		if ((byteMask & ~15) != 0) {
			throw new IllegalArgumentException("invalid byte mask: " + byteMask);
		}
		SlaveEntry slaveEntry = getMatchingEntry(address);
		if (slaveEntry != null) {
			slaveEntry.write(address, data, byteMask);
		}
	}

}
