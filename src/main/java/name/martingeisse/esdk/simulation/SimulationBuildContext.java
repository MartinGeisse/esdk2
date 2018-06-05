package name.martingeisse.esdk.simulation;

import name.martingeisse.esdk.model.ModelObject;

/**
 *
 */
public interface SimulationBuildContext {

	<SIM> void registerModel(ModelObject<SIM> origin, SIM simulationModel);

	<SIM> SIM getModel(ModelObject<SIM> origin);

}
