package name.martingeisse.esdk.core.rtl.memory;

import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.signal.custom.RtlCustomVectorSignal;
import name.martingeisse.esdk.core.util.Matrix;

/**
 *
 */
class MemoryImplementationUtil {

	// prevent instantiation
	private MemoryImplementationUtil() {
	}

	static void checkAddressSignal(RtlVectorSignal addressSignal, int rowCount) {
		if (addressSignal.getWidth() > 30) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() + " not supported");
		}
		if (1 << addressSignal.getWidth() > rowCount) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() +
				" is too large for matrix row count " + rowCount);
		}
	}

}
