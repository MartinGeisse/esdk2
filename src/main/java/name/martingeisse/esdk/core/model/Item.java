/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model;

/**
 * Materialization: items are asked to materialize before simulation or synthesis. This may produce new items, which
 * are in turn asked to materialize, and so on. All this may only affect the implementation of an item, NOT the
 * interaction with other items.
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

	protected void materialize() {
	}

	protected void initializeSimulation() {
	}

	protected final void fire(Runnable callback, long ticks) {
		design.fire(callback, ticks);
	}

}
