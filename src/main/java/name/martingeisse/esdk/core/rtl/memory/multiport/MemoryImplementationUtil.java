package name.martingeisse.esdk.core.rtl.memory.multiport;

import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.AuxiliaryFileFactory;
import name.martingeisse.esdk.core.util.Matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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

	static void generateMif(AuxiliaryFileFactory auxiliaryFileFactory, String filename, Matrix matrix) {
		if (filename == null) {
			throw new IllegalArgumentException("filename argument is null");
		}
		if (matrix == null) {
			throw new IllegalArgumentException("matrix argument is null");
		}
		try (OutputStream outputStream = auxiliaryFileFactory.create(filename)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII)) {
				matrix.writeToMif(new PrintWriter(outputStreamWriter));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
