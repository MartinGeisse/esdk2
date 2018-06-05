package name.martingeisse.esdk.simulation;

/**
 *
 */
public abstract class AbstractSimulationModel {

	private final SimulationContext context;

	public AbstractSimulationModel(SimulationContext context) {
		this.context = context;
	}

	protected final void fire(Runnable callback, long ticks) {
		context.fire(callback, ticks);
	}

}
