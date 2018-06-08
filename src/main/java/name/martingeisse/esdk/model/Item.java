/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.model;

import java.util.function.BiConsumer;

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

	public abstract SimulationModelContribution buildSimulationModel();

	/**
	 * When the simulation model gets built, first all contributions are collected. All contributions are then asked to
	 * register simulation objects. Finally, all contributions are asked to initialize their simulation objects. This
	 * order guarantees that dependency objects are available during initialization (though they may not yet be
	 * initialized). Also, wrapping registration and initialization in a contribution object helps in writing the
	 * code for registration and initialization.
	 * <p>
	 * A typical instance of this interface builds the simulation objects in its constructor, passes them to the
	 * consumer argument in {@link #registerSimulationObjects(BiConsumer)} and initializes their state in
	 * {@link #initializeSimulationObjects(DependencyProvider)}.
	 */
	public interface SimulationModelContribution {

		<T> void registerSimulationObjects(BiConsumer<Simulatable<T>, T> consumer);

		void initializeSimulationObjects(DependencyProvider dependencyProvider);

		interface DependencyProvider {
			<T> T get(Simulatable<T> modelObject);
		}

	}

}
