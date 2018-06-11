package name.martingeisse.esdk.library.bus.bus32;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.model.Simulatable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A simple bus using 32-bit address and data units.
 */
public class Bus32 extends Item {

	private BusMaster master;
	private final List<SlaveEntry> slaveEntries = new ArrayList<>();

	public Bus32(Design design) {
		super(design);
	}

	public BusMaster getMaster() {
		return master;
	}

	public void setMaster(BusMaster master) {
		this.master = master;
	}

	public List<SlaveEntry> getSlaveEntries() {
		return slaveEntries;
	}

	public void addSlave(SlaveEntry entry) {
		slaveEntries.add(entry);
	}

	public void addSlave(int address, int addressMask, BusSlave slave) {
		addSlave(new SlaveEntry(address, addressMask, slave));
	}

	@Override
	public SimulationModelContribution buildSimulationModel() {
		return new SimulationModelContribution() {

			@Override
			public <T> void registerSimulationObjects(BiConsumer<Simulatable<T>, T> consumer) {

			}

			@Override
			public void initializeSimulationObjects(DependencyProvider dependencyProvider) {

			}

		};
	}

}
