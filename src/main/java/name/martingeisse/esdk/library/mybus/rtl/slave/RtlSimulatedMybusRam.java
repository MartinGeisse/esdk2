package name.martingeisse.esdk.library.mybus.rtl.slave;

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
import name.martingeisse.esdk.library.mybus.rtl.RtlMybusSlave;

/**
 * Simulation-only MB-compatible RAM. The purpose of this is to simulate RAMs with long delays, so single-clock
 * bus cycles are not supported to simplify the code.
 */
public final class RtlSimulatedMybusRam extends RtlClockedItem implements RtlMybusSlave {

	private final int addressBits;
	private final int addressMask;
	private final Matrix matrix;
	private final int delay;
	private final RtlBitSignalConnectorSampler strobeSignal;
	private final RtlBitSignalConnectorSampler writeEnableSignal;
	private final RtlVectorSignalConnectorSampler addressSignal;
	private final RtlVectorSignalConnectorSampler writeDataSignal;
	private final RtlSimulatedSettableVectorSignal readDataSignal;
	private final RtlSimulatedComputedBitSignal ackSignal;
	private int remainingDelay;

	public RtlSimulatedMybusRam(RtlClockNetwork clock, int addressBits, int delay) {
		super(clock);
		if (addressBits < 0 || addressBits > 31) {
			throw new IllegalArgumentException("invalid address width: " + addressBits);
		}
		if (delay < 1) {
			throw new IllegalArgumentException("invalid delay: " + delay);
		}
		this.addressBits = addressBits;
		this.addressMask = (1 << addressBits) - 1;
		this.matrix = new Matrix(1 << addressBits, 32);
		this.delay = delay;
		this.strobeSignal = new RtlBitSignalConnectorSampler(clock);
		this.writeEnableSignal = new RtlBitSignalConnectorSampler(clock);
		this.addressSignal = new RtlVectorSignalConnectorSampler(clock, 32);
		this.writeDataSignal = new RtlVectorSignalConnectorSampler(clock, 32);
		this.readDataSignal = new RtlSimulatedSettableVectorSignal(getRealm(), 32);
		this.ackSignal = RtlSimulatedComputedBitSignal.of(getRealm(), () -> remainingDelay == 1);
		this.remainingDelay = 0;
	}

	public int getAddressBits() {
		return addressBits;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void setStrobeSignal(RtlBitSignal strobeSignal) {
		this.strobeSignal.setConnected(strobeSignal);
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
			if (strobeSignal.getSample()) {
				remainingDelay = delay;
			}
		} else {
			remainingDelay--;
			if (remainingDelay == 0 && writeEnableSignal.getSample()) {
				matrix.setRow(getAddress(), writeDataSignal.getSample());
			}
		}
		if (remainingDelay == 1 && !writeEnableSignal.getSample()) {
			readDataSignal.setValue(matrix.getRow(getAddress()));
		}
	}

	private int getAddress() {
		// here we compensate for the address vector being 32 bits (we can handle at most 31 because int is signed)
		return addressSignal.getSample().getBitsAsInt() & addressMask;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
