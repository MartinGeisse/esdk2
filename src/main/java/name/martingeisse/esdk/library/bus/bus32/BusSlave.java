package name.martingeisse.esdk.library.bus.bus32;

import name.martingeisse.esdk.core.model.Simulatable;

/**
 *
 */
public interface BusSlave extends Simulatable<BusSlave.SimulationModel> {

	int getAddressBits();

	interface SimulationModel {

		void read(int address, ReadCallback callback);

		void write(int address, int data, WriteCallback callback);

	}

}
