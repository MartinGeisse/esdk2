package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public final class RtlVectorSwitchSignal extends RtlSwitchSignal<RtlVectorSignal> implements RtlVectorSignal {

	private final int width;

	public RtlVectorSwitchSignal(RtlRealm realm, RtlVectorSignal selector, int width) {
		super(realm, selector);
		this.width = width;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	protected void validateOnAdd(RtlVectorSignal branch) {
		if (branch.getWidth() != width) {
			throw new IllegalArgumentException("switch statement width is " + width + ", but branch width is " + branch.getWidth());
		}
	}

	@Override
	public VectorValue getValue() {
		return getCurrentlySelectedBranch().getValue();
	}

}
