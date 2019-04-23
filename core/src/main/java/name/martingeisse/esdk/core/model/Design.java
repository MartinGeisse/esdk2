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
	private final List<Item> itemsToMaterialize = new ArrayList<>();
	private Simulation simulation;

	void register(Item item) {
		items.add(item);
		itemsToMaterialize.add(item);
	}

	public Iterable<Item> getItems() {
		return items;
	}

	public void validateOrException(boolean failOnWarnings) {
		DesignValidator validator = new DesignValidator(this);
		DesignValidationResult result = validator.validate();
		for (ItemValidationResult itemResult : result.getItemResults().values()) {
			if (!itemResult.getErrors().isEmpty()) {
				throw new IllegalStateException("validation failed with errors");
			}
			if (failOnWarnings && !itemResult.getWarnings().isEmpty()) {
				throw new IllegalStateException("validation failed with warnings");
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
		materialize();
		simulation = new Simulation();
		for (Item item : items) {
			item.initializeSimulation();
		}
	}

	public void materialize() {
		List<Item> materializationChunk = new ArrayList<>();
		while (!itemsToMaterialize.isEmpty()) {
			materializationChunk.clear();
			materializationChunk.addAll(itemsToMaterialize);
			itemsToMaterialize.clear();
			for (Item item : materializationChunk) {
				item.materialize();
			}
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
