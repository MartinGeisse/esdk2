/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulator;

/**
 * General exception supertype for any exception that occurs
 * in the PicoBlaze model and indicates that modeling the
 * real PicoBlaze has failed.
 */
public class PicoblazeSimulatorException extends Exception {

	/**
	 * Constructor.
	 */
	public PicoblazeSimulatorException() {
		super();
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public PicoblazeSimulatorException(final String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param cause the exception that caused this exception
	 */
	public PicoblazeSimulatorException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 * @param cause the exception that caused this exception
	 */
	public PicoblazeSimulatorException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
