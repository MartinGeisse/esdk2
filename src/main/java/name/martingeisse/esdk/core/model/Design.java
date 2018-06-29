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
	private Simulation simulation;

	void register(Item item) {
		items.add(item);
	}

	public void fire(Runnable eventCallback, long ticks) {
		needSimulation();
		simulation.fire(eventCallback, ticks);
	}

	public void simulate() {
		needSimulation();
		for (Item item : items) {
			item.initializeSimulation();
		}
		simulation.run();
	}

	public void stopSimulation() {
		needSimulation();
		simulation.stop();
	}

	private void needSimulation() {
		if (simulation == null) {
			simulation = new Simulation();
		}
	}

}
