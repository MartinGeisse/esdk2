/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model;

import name.martingeisse.esdk.core.model.validation.ValidationContext;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Materialization: items are asked to materialize before simulation or synthesis. This may produce new items, which
 * are in turn asked to materialize, and so on. All this may only affect the implementation of an item, NOT the
 * interaction with other items.
 */
public abstract class Item {

	private static final boolean DEBUG_NONAME_ITEMS = false;

	private final Design design;
	private String name;

	public Item(Design design) {
		this.design = design;
		design.register(this);
		if (DEBUG_NONAME_ITEMS) {
			Exception e = new Exception();
			e.fillInStackTrace();
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			setName(w.toString());
		}
	}

	public final Design getDesign() {
		return design;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
		onNameChanged();
	}

	protected void onNameChanged() {
	}

	/**
	 * Validates this item. Does not have to validate linked items since we already know them from the {@link Design}.
	 */
	public void validate(ValidationContext context) {
	}

	protected void materialize() {
	}

	protected void initializeSimulation() {
	}

	protected final void fire(Runnable callback, long ticks) {
		design.fire(callback, ticks);
	}

}
