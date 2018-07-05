package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;

/**
 *
 */
public abstract class RtlAbstractBitSignal extends RtlAbstractSignal {

	private boolean value;

	public RtlAbstractBitSignal(RtlDesign design) {
		super(design);
	}

	protected final void setValue(boolean value) {
		boolean old = this.value;
		this.value = value;
		if (value != old) {
			notifyListeners();
		}
	}

	public final boolean getValue() {
		return value;
	}

}
