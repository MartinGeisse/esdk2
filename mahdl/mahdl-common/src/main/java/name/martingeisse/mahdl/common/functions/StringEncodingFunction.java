/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.functions;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmLinked;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

/**
 *
 */
public abstract class StringEncodingFunction extends FixedSignatureFunction {

	public StringEncodingFunction() {
		super(ImmutableList.of(
			ProcessedDataType.Integer.INSTANCE,
			ProcessedDataType.Text.INSTANCE
		));
	}

	@NotNull
	@Override
	protected ProcessedDataType internalCheckType(@NotNull List<ProcessedExpression> arguments, ProcessingSidekick sidekick) {
		ConstantValue value = arguments.get(0).evaluateFormallyConstant(new ProcessedExpression.FormallyConstantEvaluationContext(sidekick));
		BigInteger integerValue = value.convertToInteger();
		if (integerValue == null) {
			return ProcessedDataType.Unknown.INSTANCE;
		}
		int size;
		try {
			size = integerValue.intValueExact();
		} catch (ArithmeticException e) {
			sidekick.onError(arguments.get(0), "size too large");
			return ProcessedDataType.Unknown.INSTANCE;
		}
		return new ProcessedDataType.Matrix(size, 8);
	}

	@NotNull
	@Override
	public ConstantValue applyToConstantValues(@NotNull CmLinked errorSource, @NotNull List<ConstantValue> arguments, @NotNull ProcessedExpression.FormallyConstantEvaluationContext context) {

		// determine size
		BigInteger integerValue = arguments.get(0).convertToInteger();
		if (integerValue == null) {
			return ConstantValue.Unknown.INSTANCE;
		}
		int size;
		try {
			size = integerValue.intValueExact();
		} catch (ArithmeticException e) {
			return ConstantValue.Unknown.INSTANCE;
		}

		// encode text
		String text = arguments.get(1).convertToString();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			encode(text, byteArrayOutputStream);
		} catch (IOException e) {
			return context.error(errorSource, e.toString());
		}
		if (byteArrayOutputStream.size() > size) {
			return context.error(errorSource, "encoded text is " + byteArrayOutputStream.size() +
				" bytes, but target size is only " + size + " bytes");
		}
		byte[] data = byteArrayOutputStream.toByteArray();

		// convert bytes to bits
		BitSet bits = new BitSet();
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			for (int j = 0; j < 8; j++) {
				if ((b & 128) != 0) {
					bits.set(8 * i + j);
				}
				b = (byte) (b << 1);
			}
		}

		return new ConstantValue.Matrix(size, 8, bits);
	}

	protected abstract void encode(String text, OutputStream out) throws IOException;

}
