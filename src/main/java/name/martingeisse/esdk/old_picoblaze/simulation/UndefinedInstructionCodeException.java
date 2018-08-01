/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulation;

/**
 * This exception is thrown by {@link PicoblazeState} when
 * trying to execute an encoded instruction whose code does
 * not correspond to any real instruction.
 */
public class UndefinedInstructionCodeException extends PicoblazeSimulatorException {

	/**
	 * Constructor.
	 */
	public UndefinedInstructionCodeException() {
		super();
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public UndefinedInstructionCodeException(final String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @param cause the exception that caused this exception
	 */
	public UndefinedInstructionCodeException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * @param message the exception message
	 * @param cause the exception that caused this exception
	 */
	public UndefinedInstructionCodeException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
