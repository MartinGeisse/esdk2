package name.martingeisse.esdk.core.rtl.simulation;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorValue;

/**
 * Vector version of {@link RtlSettableSignal}.
 */
public final class RtlSettableVectorSignal extends RtlSettableSignal implements RtlVectorSignal {

	private final int width;
	private RtlVectorValue value;

	public RtlSettableVectorSignal(RtlDesign design, int width) {
		super(design);
		this.width = width;
		this.value = RtlVectorValue.zeroes(width);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public RtlVectorValue getValue() {
		return value;
	}

	public void setValue(RtlVectorValue value) {
		this.value = value;
		// TODO notify RTL simulation core
	}
}
