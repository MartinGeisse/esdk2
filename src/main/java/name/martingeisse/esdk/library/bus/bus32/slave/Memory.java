package name.martingeisse.esdk.library.bus.bus32.slave;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.simulation.SimulationContext;
import name.martingeisse.esdk.core.simulation.SimulationObject;
import name.martingeisse.esdk.library.bus.bus32.BusSlave;
import name.martingeisse.esdk.library.bus.bus32.ReadCallback;
import name.martingeisse.esdk.library.bus.bus32.WriteCallback;

import java.util.Arrays;

/**
 * A simple on-chip memory that uses the lowest N address bits to store 2^N 32-bit words.
 */
public final class Memory extends Item implements BusSlave {

	private int addressBits;
	private Initializer initializer = Initializer.ZERO;

	public Memory(Design design) {
		super(design);
	}

	public int getAddressBits() {
		return addressBits;
	}

	public void setAddressBits(int addressBits) {
		this.addressBits = addressBits;
	}

	public Initializer getInitializer() {
		return initializer;
	}

	public void setInitializer(Initializer initializer) {
		this.initializer = initializer;
	}

	@Override
	public SimulationModelContribution buildSimulationModel(SimulationContext context) {
		SimulationModel simulationModel = new SimulationModel(context, Memory.this);
		return new SimulationModelContribution() {

			@Override
			public void registerSimulationObjects(SimulationObjectRegistry registry) {
				registry.register(Memory.this, simulationModel);
			}

			@Override
			public void initializeSimulationObjects(DependencyProvider dependencyProvider) {
				simulationModel.initialize(Memory.this);
			}

		};
	}

	public interface Initializer {

		void initialize(int[] destination);

		Initializer ZERO = destination -> {
		};

		static Initializer fill(int value) {
			return destination -> Arrays.fill(destination, value);
		}

		static Initializer use(int[] data) {
			return destination -> {
				if (destination.length < data.length) {
					throw new RuntimeException("memory size (" + destination.length + ") too small for data (" + data.length + ")");
				}
				System.arraycopy(data, 0, destination, 0, data.length);
			};
		}

	}

	public static final class SimulationModel extends SimulationObject implements BusSlave.SimulationModel {

		private final int addressBits;
		private final int addressMask;
		private final int[] data;

		public SimulationModel(SimulationContext context, Memory memory) {
			super(context);
			this.addressBits = memory.getAddressBits();
			this.data = new int[1 << addressBits];
			this.addressMask = data.length - 1;
		}

		public void initialize(Memory memory) {
			memory.getInitializer().initialize(data);
		}

		public int getAddressBits() {
			return addressBits;
		}

		public int getSize() {
			return data.length;
		}

		public int getValue(int address) {
			return data[address];
		}

		public void setValue(int address, int value) {
			data[address] = value;
		}

		@Override
		public void read(int address, ReadCallback callback) {
			// no delay for now
			fire(() -> callback.onReadFinished(getValue(address & addressMask)), 0);
		}

		@Override
		public void write(int address, int data, WriteCallback callback) {
			// no delay for now
			fire(() -> {
				setValue(address & addressMask, data);
				callback.onWriteFinished();
			}, 0);
		}

	}
}
