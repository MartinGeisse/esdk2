/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.functions;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.Environment;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.common.util.HeadBodyReader;
import name.martingeisse.mahdl.common.util.LiteralParser;
import name.martingeisse.mahdl.input.cm.CmLinked;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public final class LoadMahdlMatrixFileFunction extends FixedSignatureFunction {

	private static final Pattern ROW_PATTERN = Pattern.compile("[0-9a-fA-F]+");

	public LoadMahdlMatrixFileFunction() {
		super(ImmutableList.of(
			ProcessedDataType.Text.INSTANCE,
			ProcessedDataType.Integer.INSTANCE,
			ProcessedDataType.Integer.INSTANCE
		));
	}

	@NotNull
	@Override
	public String getName() {
		return "loadMatrix";
	}

	@NotNull
	@Override
	protected ProcessedDataType internalCheckType(@NotNull List<ProcessedExpression> arguments, ProcessingSidekick sidekick) {
		ProcessedExpression.FormallyConstantEvaluationContext context = new ProcessedExpression.FormallyConstantEvaluationContext(sidekick);
		int firstSize = arguments.get(1).evaluateFormallyConstant(context).convertToInteger().intValueExact();
		int secondSize = arguments.get(2).evaluateFormallyConstant(context).convertToInteger().intValueExact();
		return new ProcessedDataType.Matrix(firstSize, secondSize);
	}

	@NotNull
	@Override
	public ConstantValue applyToConstantValues(@NotNull CmLinked errorSource, @NotNull List<ConstantValue> arguments, @NotNull ProcessedExpression.FormallyConstantEvaluationContext context) {
		String filename = arguments.get(0).convertToString();
		int rows = arguments.get(1).convertToInteger().intValueExact();
		int columns = arguments.get(2).convertToInteger().intValueExact();

		// read the file
		MutableObject<BitSet> resultBitSetHolder = new MutableObject<>();
		try (InputStream inputStream = Environment.Holder.INSTANCE.openDataFile(errorSource.getCmNode(), filename)) {
			try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
				new HeadBodyReader() {

					private boolean rowsOk = false, columnsOk = false;
					private BitSet bits;
					private int firstEmptyBodyLine = -1;

					@Override
					protected void onHeadProperty(String key, String value) throws FormatException {
						switch (key) {

							case "rows":
								if (expectNonNegativeInteger(key, value) != rows) {
									throw new FormatException("mismatching number of rows");
								}
								rowsOk = true;
								break;

							case "columns":
								if (expectNonNegativeInteger(key, value) != columns) {
									throw new FormatException("mismatching number of columns");
								}
								columnsOk = true;
								break;

							default:
								throw new FormatException("unknown property: " + key);

						}
					}

					@Override
					protected void onStartBody() throws FormatException {
						if (!rowsOk) {
							throw new FormatException("missing 'rows' property");
						}
						if (!columnsOk) {
							throw new FormatException("missing 'columns' property");
						}
						bits = new BitSet(rows * columns);
						resultBitSetHolder.setValue(bits);
					}

					@Override
					protected void onBodyLine(int totalLineIndex, int bodyLineIndex, String line) throws FormatException {
						line = line.trim();
						if (line.isEmpty()) {
							if (firstEmptyBodyLine == -1) {
								firstEmptyBodyLine = totalLineIndex;
							}
							return;
						}
						if (firstEmptyBodyLine != -1) {
							throw new FormatException("body contains empty line(s) starting at line " + firstEmptyBodyLine);
						}
						if (!ROW_PATTERN.matcher(line).matches()) {
							throw new FormatException("invalid value at line " + totalLineIndex);
						}
						ConstantValue.Vector rowValue;
						try {
							rowValue = LiteralParser.parseVector(columns + "h" + line);
						} catch (LiteralParser.ParseException e) {
							// shouldn't happen since that line passed the ROW_PATTERN already
							throw new FormatException("unexpected exception while parsing line " + totalLineIndex + ": " + e.toString());
						}
						BitSet rowBits = rowValue.getBits();
						int rowBaseIndex = bodyLineIndex * columns;
						for (int i = 0; i < columns; i++) {
							bits.set(rowBaseIndex + i, rowBits.get(i));
						}
					}

					private int expectNonNegativeInteger(String key, String text) throws FormatException {
						int value;
						try {
							value = Integer.parseInt(text);
						} catch (NumberFormatException e) {
							throw new FormatException("invalid value for property '" + key + "'");
						}
						if (value < 0) {
							throw new FormatException("property '" + key + "' cannot be negative");
						}
						return value;
					}

				}.readFrom(inputStreamReader);
			}
		} catch (HeadBodyReader.FormatException e) {
			return context.error(errorSource, e.getMessage());
		} catch (IOException e) {
			return context.error(errorSource, e.toString());
		}
		return new ConstantValue.Matrix(rows, columns, resultBitSetHolder.getValue());
	}

}
