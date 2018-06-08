package name.martingeisse.esdk.simulation;

import name.martingeisse.esdk.model.Item;

/**
 * Base implementation for simulation objects, i.e. the objects returned by {@link Item.SimulationModelContribution#}
 */
public abstract class SimulationObject {

	private final SimulationContext context;

	public SimulationObject(SimulationContext context) {
		this.context = context;
	}

	protected final void fire(Runnable callback, long ticks) {
		context.fire(callback, ticks);
	}

}
