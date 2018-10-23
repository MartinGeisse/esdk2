package name.martingeisse.esdk.library.bus.wishbone.ram;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnectorSampler;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnectorSampler;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedComputedBitSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedSettableVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.library.bus.wishbone.WishboneSimpleSlave;

/**
 * Simulation-only WB-compatible RAM. The purpose of this is to simulate RAMs with long delays, so single-clock
 * bus cycles are not supported to simplify the code.
 */
public final class SimulatedDelayedWishboneRam32 extends RtlClockedItem implements WishboneSimpleSlave {

	private final int addressBits;
	private final Matrix matrix;
	private final int delay;
	private final RtlBitSignalConnectorSampler cycleStrobeSignal;
	private final RtlBitSignalConnectorSampler writeEnableSignal;
	private final RtlVectorSignalConnectorSampler addressSignal;
	private final RtlVectorSignalConnectorSampler writeDataSignal;
	private final RtlSimulatedSettableVectorSignal readDataSignal;
	private final RtlSimulatedComputedBitSignal ackSignal;
	private int remainingDelay;

	public SimulatedDelayedWishboneRam32(RtlClockNetwork clock, int addressBits, int delay) {
		super(clock);
		if (addressBits < 0 || addressBits > 31) {
			throw new IllegalArgumentException("invalid address width: " + addressBits);
		}
		if (delay < 1) {
			throw new IllegalArgumentException("invalid delay: " + delay);
		}
		this.addressBits = addressBits;
		this.matrix = new Matrix(1 << addressBits, 32);
		this.delay = delay;
		this.cycleStrobeSignal = new RtlBitSignalConnectorSampler(clock);
		this.writeEnableSignal = new RtlBitSignalConnectorSampler(clock);
		this.addressSignal = new RtlVectorSignalConnectorSampler(clock, addressBits);
		this.writeDataSignal = new RtlVectorSignalConnectorSampler(clock, 32);
		this.readDataSignal = new RtlSimulatedSettableVectorSignal(getRealm(), 32);
		this.ackSignal = RtlSimulatedComputedBitSignal.of(getRealm(), () -> remainingDelay == 1);
	}

	public int getAddressBits() {
		return addressBits;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void setCycleStrobeSignal(RtlBitSignal cycleStrobeSignal) {
		this.cycleStrobeSignal.setConnected(cycleStrobeSignal);
	}

	@Override
	public void setWriteEnableSignal(RtlBitSignal writeEnableSignal) {
		this.writeEnableSignal.setConnected(writeEnableSignal);
	}

	@Override
	public void setAddressSignal(RtlVectorSignal addressSignal) {
		this.addressSignal.setConnected(addressSignal);
	}

	@Override
	public void setWriteDataSignal(RtlVectorSignal writeDataSignal) {
		this.writeDataSignal.setConnected(writeDataSignal);
	}

	@Override
	public RtlVectorSignal getReadDataSignal() {
		return readDataSignal;
	}

	@Override
	public RtlBitSignal getAckSignal() {
		return ackSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
	}

	@Override
	public void updateState() {
		if (remainingDelay == 0) {
			if (cycleStrobeSignal.getSample()) {
				remainingDelay = delay;
			}
		} else {
			remainingDelay--;
			if (remainingDelay == 0) {
				int address = addressSignal.getSample().getAsUnsignedInt();
				if (writeEnableSignal.getSample()) {
					matrix.setRow(address, writeDataSignal.getSample());
				} else {
					readDataSignal.setValue(matrix.getRow(address));
				}
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
