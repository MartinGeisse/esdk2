package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRegion;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 * Bit version of {@link RtlSettableSignal}.
 */
public final class RtlSettableBitSignal extends RtlSettableSignal implements RtlBitSignal {

	private boolean value;

	public RtlSettableBitSignal(RtlRegion region) {
		super(region);
	}

	@Override
	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
		// TODO notify RTL simulation core
	}

}
