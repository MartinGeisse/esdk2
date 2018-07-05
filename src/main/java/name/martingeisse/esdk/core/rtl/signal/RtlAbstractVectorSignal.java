package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public abstract class RtlAbstractVectorSignal extends RtlAbstractSignal {

	private final int width;
	private VectorValue value;

	public RtlAbstractVectorSignal(RtlDesign design, int width) {
		super(design);
		this.width = width;
		this.value = VectorValue.ofUnsigned(width, 0);
	}

	protected final void setValue(VectorValue value) {
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("new value has wrong width " + value.getWidth() + ", expected " + width);
		}
		VectorValue old = this.value;
		this.value = value;
		if (!value.equals(old)) {
			notifyListeners();
		}
	}

	public VectorValue getValue() {
		return value;
	}

}
