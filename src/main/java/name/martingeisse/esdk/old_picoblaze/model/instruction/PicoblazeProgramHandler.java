/**
 * Copyright (c) 2015 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.model.instruction;

/**
 * Strategy that defines how instructions are loaded.
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

