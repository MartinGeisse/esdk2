/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulator.port;

import name.martingeisse.old.esdk.model.picoblaze.simulator.IPicoblazePortHandler;

/**
 * PicoBlaze port handler for an {@link IGenericBusSlave}. Note that
 * instances are usually created by convenience methods, for example
 * by {@link AggregatePicoblazePortHandler}.
 */
public class GenericBusSlavePortHandler implements IPicoblazePortHandler {

	/**
	 * the slave
	 */
	private IGenericBusSlave slave;

	/**
	 * Constructor.
	 */
	public GenericBusSlavePortHandler() {
	}

	/**
	 * Constructor.
	 * @param slave the bus slave to wrap
	 */
	public GenericBusSlavePortHandler(final IGenericBusSlave slave) {
		this.slave = slave;
	}

	/**
	 * Getter method for the slave.
	 * @return the slave
	 */
	public IGenericBusSlave getSlave() {
		return slave;
	}

	/**
	 * Setter method for the slave.
	 * @param slave the slave to set
	 */
	public void setSlave(final IGenericBusSlave slave) {
		this.slave = slave;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.esdk.picoblaze.simulator.IPicoblazePortHandler#handleInput(int)
	 */
	@Override
	public int handleInput(final int address) {
		return (isAddressValid(address) ? slave.read(address) : 0);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.esdk.picoblaze.simulator.IPicoblazePortHandler#handleOutput(int, int)
	 */
	@Override
	public void handleOutput(final int address, final int value) {
		if (isAddressValid(address)) {
			slave.write(address, value);
		}
	}

	/**
	 * Checks whether the specified address is valid according the slave's number of address bits.
	 * If no slave is set, all addresses are invalid.
	 *
	 * @param address the address to check
	 * @return true if valid, false if invalid
	 */
	private boolean isAddressValid(final int address) {
		return (slave != null) && (address < (1 << slave.getLocalAddressBitCount()));
	}

}
