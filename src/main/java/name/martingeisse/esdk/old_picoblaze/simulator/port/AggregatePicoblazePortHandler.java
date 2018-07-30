/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulator.port;

import name.martingeisse.old.esdk.model.picoblaze.simulator.IPicoblazePortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This port handler stores a list of sub-handlers, each with a
 * base address and address count. Each I/O operation will be
 * passed on to the first registered handler for the address,
 * with the base address subtracted from the port address used
 * by the I/O instruction.
 *
 * If no handler can be found for a certain address, then this
 * handler returns 0 on read operations and does nothing on
 * write operations.
 */
public final class AggregatePicoblazePortHandler implements IPicoblazePortHandler {

	/**
	 * Represents an entry in the list of registered handlers.
	 */
	public class Entry {

		/**
		 * the baseAddress
		 */
		private final int baseAddress;

		/**
		 * the addressCount
		 */
		private final int addressCount;

		/**
		 * the handler
		 */
		private final IPicoblazePortHandler handler;

		/**
		 * Constructor.
		 * @param baseAddress the base address
		 * @param addressCount the number of addresses used by this entry
		 * @param handler the handler to invoke
		 */
		public Entry(final int baseAddress, final int addressCount, final IPicoblazePortHandler handler) {
			this.baseAddress = baseAddress;
			this.addressCount = addressCount;
			this.handler = handler;
		}

		/**
		 * Getter method for the baseAddress.
		 * @return the baseAddress
		 */
		public int getBaseAddress() {
			return baseAddress;
		}

		/**
		 * Getter method for the addressCount.
		 * @return the addressCount
		 */
		public int getAddressCount() {
			return addressCount;
		}

		/**
		 * Getter method for the handler.
		 * @return the handler
		 */
		public IPicoblazePortHandler getHandler() {
			return handler;
		}

	}

	/**
	 * the entries
	 */
	private List<Entry> entries;

	/**
	 * Constructor.
	 */
	public AggregatePicoblazePortHandler() {
		this.entries = new ArrayList<Entry>();
	}

	/**
	 * Getter method for the entries.
	 * @return the entries
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * Setter method for the entries.
	 * @param entries the entries to set
	 */
	public void setEntries(final List<Entry> entries) {
		this.entries = entries;
	}

	/**
	 * Convenience method to add an entry.
	 * @param baseAddress the base address
	 * @param addressCount the number of addresses used by this entry
	 * @param handler the handler to invoke
	 */
	public void addEntry(final int baseAddress, final int addressCount, final IPicoblazePortHandler handler) {
		entries.add(new Entry(baseAddress, addressCount, handler));
	}

	/**
	 * Convenience method to add an entry for a generic bus slave.
	 * @param baseAddress the base address
	 * @param slave the salve to add
	 */
	public void addEntry(final int baseAddress, final IGenericBusSlave slave) {
		addEntry(baseAddress, 1 << slave.getLocalAddressBitCount(), new GenericBusSlavePortHandler(slave));
	}

	/**
	 * Returns the handler that is responsible for handling the specified address,
	 * i.e. the first handler whose range covers the address.
	 * @param address the address to handle
	 * @return the handler for that address, or null if none could be found
	 */
	public Entry getEntryForAddress(final int address) {
		for (final Entry entry : entries) {
			if (address >= entry.baseAddress && address < entry.baseAddress + entry.addressCount) {
				return entry;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.esdk.picoblaze.simulator.IPicoblazePortHandler#handleInput(int)
	 */
	@Override
	public int handleInput(final int address) {
		final Entry entry = getEntryForAddress(address);
		return (entry == null ? 0 : entry.handler.handleInput(address - entry.baseAddress));
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.esdk.picoblaze.simulator.IPicoblazePortHandler#handleOutput(int, int)
	 */
	@Override
	public void handleOutput(final int address, final int value) {
		final Entry entry = getEntryForAddress(address);
		if (entry != null) {
			entry.handler.handleOutput(address - entry.baseAddress, value);
		}
	}

}
