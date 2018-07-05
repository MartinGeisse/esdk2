package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Note for subclasses: This signal should be registered as a listener for all its input signals, so it gets updated
 * when one of the inputs changes. Subclasses must implement {@link #onSignalChanged(RtlSignal)} to call
 * {@link RtlAbstractBitSignal#setValue(boolean)} or {@link RtlAbstractVectorSignal#setValue(VectorValue)}, which in
 * turn calls {@link #notifyListeners()} if the value changed. There is normally no reason to call notifyListeners()
 * directly. TODO when package structure has stabilized, make that method package-private
 */
public abstract class RtlAbstractSignal extends RtlItem implements RtlSignal, RtlSignalListener {

	private RtlSignalListener listeners;

	public RtlAbstractSignal(RtlDesign design) {
		super(design);
	}

	protected final void notifyListeners() {
		if (listeners != null) {
			listeners.onSignalChanged(this);
		}
	}

}
