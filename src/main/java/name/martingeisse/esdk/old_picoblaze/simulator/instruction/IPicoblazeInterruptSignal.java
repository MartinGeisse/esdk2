/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulator.instruction;

/**
 * A model for the interrupt signal used by the PicoBlaze.
 */
public interface IPicoblazeInterruptSignal {

	/**
	 * Returns true if the interrupt signal is active, false if not.
	 * @return whether the interrupt signal is active.
	 */
	boolean isActive();

}
