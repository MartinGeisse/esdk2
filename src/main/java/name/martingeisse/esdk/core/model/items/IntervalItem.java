/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model.items;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;

/**
 *
 */
public final class IntervalItem extends Item {

	private final long initialDelay;
	private final long period;
	private final Runnable callback;

	public IntervalItem(Design design, long initialDelay, long period, Runnable callback) {
		super(design);
		this.initialDelay = initialDelay;
		this.period = period;
		this.callback = callback;
	}

	@Override
	protected void initializeSimulation() {
		fire(this::handle, initialDelay);
	}

	private void handle() {
		callback.run();
		fire(this::handle, period);
	}

}
