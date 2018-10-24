/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.model;

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

	public void simulate() {
		needSimulation();
		materialize();
		for (Item item : items) {
			item.initializeSimulation();
		}
		simulation.run();
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
			simulation = new Simulation();
		}
	}

}
