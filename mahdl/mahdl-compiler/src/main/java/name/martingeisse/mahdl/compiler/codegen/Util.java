package name.martingeisse.mahdl.compiler.codegen;

import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 */
final class Util {

	// prevent instantiation
	private Util() {
	}

	static String valueTypeToString(ProcessedDataType type) {
		switch (type.getFamily()) {

			case BIT:
				return "boolean";

			case VECTOR:
				return "VectorValue";

			case MATRIX:
				return "Matrix";

			case INTEGER:
				return "int";

			case TEXT:
				return "String";

			case CLOCK:
				return "RtlClockNetwork";

			default:
				return "Object";

		}
	}

	static String signalTypeToString(ProcessedDataType type) {
		switch (type.getFamily()) {

			case BIT:
				return "RtlBitSignal";

			case VECTOR:
				return "RtlVectorSignal";

			case CLOCK:
				return "RtlClockNetwork";

			default:
				return "Object";

		}
	}

	static String buildJavaStringLiteral(String value) {
		return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	static byte[] generateMatrixFileContents(ConstantValue.Matrix matrix) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8)) {
				try (PrintWriter out = new PrintWriter(outputStreamWriter)) {
					out.println("rows: " + matrix.getFirstSize());
					out.println("columns: " + matrix.getSecondSize());
					out.println();
					for (int rowIndex = 0; rowIndex < matrix.getFirstSize(); rowIndex++) {
						ConstantValue.Vector row = (ConstantValue.Vector) matrix.selectIndex(rowIndex);
						out.println(row.getHexLiteral());
					}
				}
			}
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
