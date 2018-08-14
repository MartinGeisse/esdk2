package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.Matrix;

/**
 *
 */
public final class RtlAsynchronouslyReadSynchronouslyWrittenRam extends RtlAbstractSynchronousRam {

	public RtlAsynchronouslyReadSynchronouslyWrittenRam(RtlClockNetwork clockNetwork, Matrix matrix) {
		super(clockNetwork, matrix);
	}

	public RtlAsynchronouslyReadSynchronouslyWrittenRam(RtlClockNetwork clockNetwork, int rowCount, int columnCount) {
		super(clockNetwork, rowCount, columnCount);
	}

	@Override
	public RtlVectorSignal getReadDataSignal() {
		return RtlCustomVectorSignal.of(getRealm(), getMatrix().getColumnCount(),
			() -> getMatrix().getRow(getAddressSignal().getValue().getAsUnsignedInt()));
	}

}
