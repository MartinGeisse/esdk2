/**
 * Copyright (c) 2015 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulation.port;

import name.martingeisse.esdk.old_picoblaze.simulation.Picoblaze;

/**
 * Strategy that defines how the environment reacts to INPUT and OUTPUT instructions executed by a PicoBlaze.
 * <p>
 * This interface is meant to define port behavior at a high level. To interact with ports at RTL level, use the
 * corresponding address and read/write strobe signals from the {@link Picoblaze} and install an {@link PicoblazePortHandlerRtl}
 * implementation of this interface to define the source for input data.
 */
public interface PicoblazePortHandler {

	/**
	 * Handles an INPUT instruction from the specified address.
	 * @param address the address (0-255)
	 * @return the value (only the lowest 8 bits are respected)
	 */
	int handleInput(int address);

	/**
	 * Handles an OUTPUT instruction to the specified address.
	 * @param address the address (0-255)
	 * @param value the value (0-255)
	 */
	void handleOutput(int address, int value);

}

