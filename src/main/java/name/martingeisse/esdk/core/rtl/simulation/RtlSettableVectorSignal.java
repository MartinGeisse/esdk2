package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Vector version of {@link RtlSettableSignal}.
 */
public final class RtlSettableVectorSignal extends RtlSettableSignal implements RtlVectorSignal {

	private final int width;
	private VectorValue value;

	public RtlSettableVectorSignal(RtlRealm realm, int width) {
		super(realm);
		this.width = width;
		this.value = VectorValue.ofUnsigned(width, 0);
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VectorValue getValue() {
		return value;
	}

	public void setValue(VectorValue value) {
		this.value = value;
	}

}
