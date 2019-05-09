package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Vector version of {@link RtlSimulatedSettableSignal}.
 */
public final class RtlSimulatedSettableVectorSignal extends RtlSimulatedSettableSignal implements RtlVectorSignal {

	private final int width;
	private VectorValue value;

	public RtlSimulatedSettableVectorSignal(RtlRealm realm, int width) {
		super(realm);
		this.width = width;
		this.value = VectorValue.of(width, 0);
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
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("get vector value of wrong width " + value.getWidth() + ", expected " + width);
		}
		this.value = value;
	}

}
