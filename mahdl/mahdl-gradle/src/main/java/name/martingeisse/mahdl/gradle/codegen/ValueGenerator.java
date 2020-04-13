package name.martingeisse.mahdl.gradle.codegen;

import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.gradle.CompilationErrors;
import name.martingeisse.mahdl.gradle.model.GenerationModel;
import name.martingeisse.mahdl.input.cm.CmLinked;

/**
 *
 */
public class ValueGenerator {

	private final GenerationModel model;
	private final StringBuilder builder;
	private final CodeGenerator.DataFileFactory dataFileFactory;

	public ValueGenerator(GenerationModel model, StringBuilder builder, CodeGenerator.DataFileFactory dataFileFactory) {
		this.model = model;
		this.builder = builder;
		this.dataFileFactory = dataFileFactory;
	}

	/**
	 * Like buildValue(), but handles multiple values and returns them as a comma-separated string.
	 */
	public String buildValues(Iterable<? extends ConstantValue> values) {
		StringBuilder valueBuilder = new StringBuilder();
		boolean first = true;
		for (ConstantValue value : values) {
			if (first) {
				first = false;
			} else {
				valueBuilder.append(", ");
			}
			valueBuilder.append(buildValue(value));
		}
		return valueBuilder.toString();
	}

	/**
	 * For a MaHDL constant value, builds a Java expression to be used in an ESDK module constructor.
	 * The expression evaluates to a value of the corresponding ESDK type.
	 * <p>
	 * This method may use the string builder provided in the constructor to build helper values as Java
	 * constructor statements; therefore, this method must not be called while building a Java statement is in progress.
	 */
	public String buildValue(ConstantValue value) {
		if (value instanceof ConstantValue.Bit) {
			return ((ConstantValue.Bit) value).isSet() ? "true" : "false";
		} else if (value instanceof ConstantValue.Vector) {
			ConstantValue.Vector vector = (ConstantValue.Vector) value;
			if (vector.getSize() > 64) {
				throw new UnsupportedOperationException("constant vector size >64 not yet supported");
			}
			return "VectorValue.of(" + vector.getSize() + ", " + vector.convertToInteger().longValue() + "L)";
		} else if (value instanceof ConstantValue.Matrix) {
			ConstantValue.Matrix matrix = (ConstantValue.Matrix)value;
			String name = "___matrix" + model.newSyntheticConstruct();
			if (matrix.getFirstSize() * matrix.getSecondSize() < 10_000) {
				builder.append("		Matrix ").append(name).append(" = new Matrix(").append(matrix.getFirstSize())
					.append(", ").append(matrix.getSecondSize()).append(");\n");
				for (int i = 0; i < matrix.getFirstSize(); i++) {
					ConstantValue row = matrix.selectIndex(i);
					if (row instanceof ConstantValue.Vector) {
						builder.append("		").append(name).append(".setRow(").append(i).append(", ")
							.append(buildValue(row)).append(");\n");

					}
				}
			} else {
				dataFileFactory.createDataFile(name, Util.generateMatrixFileContents(matrix));
				builder.append("		Matrix ").append(name).append(" = Matrix.load(")
					.append(dataFileFactory.getAnchorClassName()).append(".class, \"").append(name)
					.append("\", ").append(matrix.getFirstSize()).append(", ").append(matrix.getSecondSize())
					.append(");\n");
			}
			return name;
		} else if (value instanceof ConstantValue.Integer) {
			return ((ConstantValue.Integer) value).getValue().toString();
		} else if (value instanceof ConstantValue.Text) {
			return Util.buildJavaStringLiteral(((ConstantValue.Text) value).getValue());
		} else {
			CompilationErrors.reportError(null, "unsupported run-time value: " + value);
			return "null";
		}
	}

}
