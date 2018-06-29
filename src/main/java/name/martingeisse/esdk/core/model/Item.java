/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model;

/**
 *
 */
public abstract class Item {

	private final Design design;

	public Item(Design design) {
		this.design = design;
		design.register(this);
	}

	public final Design getDesign() {
		return design;
	}

	protected void initializeSimulation() {
	}

	protected final void fire(Runnable callback, long ticks) {
		design.fire(callback, ticks);
	}

}
