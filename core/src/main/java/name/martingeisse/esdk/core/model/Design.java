/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model;

import name.martingeisse.esdk.core.model.validation.DesignValidationResult;
import name.martingeisse.esdk.core.model.validation.DesignValidator;
import name.martingeisse.esdk.core.model.validation.ItemValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Design {

	private final List<Item> items = new ArrayList<>();
	private Simulation simulation;

	void register(Item item) {
		items.add(item);
	}

	public Iterable<Item> getItems() {
		return items;
	}

	public DesignValidationResult validate() {
		return new DesignValidator(this).validate();
	}

	public void validateOrException(boolean failOnWarnings) {
		for (ItemValidationResult itemResult : validate().getItemResults().values()) {
			if (!itemResult.getErrors().isEmpty()) {
				throw new IllegalStateException("validation failed with errors for item " + itemResult.getItem() + ": " + itemResult.getErrors());
			}
			if (failOnWarnings && !itemResult.getWarnings().isEmpty()) {
				throw new IllegalStateException("validation failed with warnings for item " + itemResult.getItem() + ": " + itemResult.getErrors());
			}
		}
	}

	public void simulate() {
		prepareSimulation();
		continueSimulation();
	}

	public void prepareSimulation() {
		if (simulation != null) {
			throw new IllegalStateException("simulation already prepared");
		}
		validateOrException(false);
		simulation = new Simulation();
		for (Item item : items) {
			item.initializeSimulation();
		}
	}

	public void continueSimulation() {
		needSimulation();
		simulation.run();
	}

	public void stopSimulation() {
		needSimulation();
		simulation.stop();
	}

	public void fire(Runnable eventCallback, long ticks) {
		needSimulation();
		simulation.fire(eventCallback, ticks);
	}

	private void needSimulation() {
		if (simulation == null) {
			throw new IllegalStateException("simulation not prepared");
		}
	}

}
