/**
 * Copyright (c) 2015 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulation.program;

import name.martingeisse.esdk.old_picoblaze.simulation.Picoblaze;

/**
 * Strategy that defines how instructions are loaded.
 * <p>
 * This interface is meant to define port behavior at a high level. To interact at RTL level, use the
 * corresponding address signal from the {@link Picoblaze} and install an {@link PicoblazeProgramHandlerRtl}
 * implementation of this interface to define the source for input data.
 */
public interface PicoblazeProgramHandler {

	/**
	 * Reads an instruction from the specified address.
	 *
	 * @param address the address (0-1023)
	 * @return the instruction (only the lowest 18 bits are respected)
	 */
	int readInstruction(int address);

}

