package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlBitSwitchSignal extends RtlSwitchSignal<RtlBitSignal> implements RtlBitSignal {

	public RtlBitSwitchSignal(RtlRealm realm, RtlVectorSignal selector) {
		super(realm, selector);
	}

	@Override
	protected void validateOnAdd(RtlBitSignal branch) {
	}

	@Override
	public boolean getValue() {
		return getCurrentlySelectedBranch().getValue();
	}

}
