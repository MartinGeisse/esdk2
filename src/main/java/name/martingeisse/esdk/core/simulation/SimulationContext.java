package name.martingeisse.esdk.core.simulation;

/**
 *
 */
public interface SimulationContext {

	void fire(Runnable eventCallback, long ticks);

}
