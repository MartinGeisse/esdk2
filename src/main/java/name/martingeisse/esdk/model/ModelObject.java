package name.martingeisse.esdk.model;

import name.martingeisse.esdk.simulation.SimulationBuildContext;
import name.martingeisse.esdk.simulation.SimulationContext;

/**
 *
 */
public interface ModelObject<SIM> {

	SIM createSimulationModel(SimulationContext context);

	void initializeSimulationModel(SIM simulationModel, SimulationBuildContext context);

}
