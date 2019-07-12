package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 * Bit version of {@link RtlSimulatedRegister}.
 */
public final class RtlSimulatedBitRegister extends RtlSimulatedRegister implements RtlBitSignal {

	private boolean value;
	private boolean nextValue;

	public RtlSimulatedBitRegister(RtlClockNetwork clock) {
		super(clock);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public boolean getNextValue() {
		return nextValue;
	}

	public void setNextValue(boolean nextValue) {
		this.nextValue = nextValue;
	}

	@Override
	public void computeNextState() {
		// must be manually set from the outside
	}

	@Override
	public void updateState() {
		this.value = nextValue;
	}

}
