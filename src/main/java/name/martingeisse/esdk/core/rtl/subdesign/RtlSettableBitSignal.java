package name.martingeisse.esdk.core.rtl.subdesign;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 * Bit version of {@link RtlSettableSignal}.
 */
public final class RtlSettableBitSignal extends RtlSettableSignal implements RtlBitSignal {

	private boolean value;

	public RtlSettableBitSignal(RtlDesign design) {
		super(design);
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
