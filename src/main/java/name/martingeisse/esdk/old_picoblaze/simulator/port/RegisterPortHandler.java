/**
 * Copyright (c) 2015 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.old_picoblaze.simulator.port;

import name.martingeisse.esdk.old_picoblaze.simulator.IPicoblazePortHandler;

/**
 * Represents a single register that reacts to all I/O operations.
 * The address is ignored by this handler.
 */
public class RegisterPortHandler implements IPicoblazePortHandler {

	/**
	 * the value
	 */
	private int value;

	/**
	 * Constructor.
	 */
	public RegisterPortHandler() {
	}

	/**
	 * Getter method for the value.
	 * @return the value
	 */
	public final int getValue() {
		return value;
	}

	/**
	 * Setter method for the value.
	 * @param value the value to set
	 */
	public final void setValue(final int value) {
		this.value = value;
		onChange(false);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.esdk.picoblaze.simulator.IPicoblazePortHandler#handleInput(int)
	 */
	@Override
	public final int handleInput(final int address) {
		return value;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.esdk.picoblaze.simulator.IPicoblazePortHandler#handleOutput(int, int)
	 */
	@Override
	public final void handleOutput(final int address, final int value) {
		this.value = value;
		onChange(true);
	}

	/**
	 * This method is invoked whenever the value is changed by either setValue()
	 * or an output instruction.
	 * @param wasInstruction if true, then the change was caused by an OUTPUT instruction
	 * (technically, by the handleOutput() method). If false, the change was caused by
	 * the setValue() method.
	 */
	protected void onChange(final boolean wasInstruction) {
	}

}
