package name.martingeisse.esdk.simulation;

/**
 *
 */
public interface SimulationContext {

	void fire(Runnable eventCallback, long ticks);

}
