package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 * Bit version of {@link RtlSimulatedSettableSignal}.
 */
public final class RtlSimulatedSettableBitSignal extends RtlSimulatedSettableSignal implements RtlBitSignal {

	private boolean value;

	public RtlSimulatedSettableBitSignal(RtlRealm realm) {
		super(realm);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
