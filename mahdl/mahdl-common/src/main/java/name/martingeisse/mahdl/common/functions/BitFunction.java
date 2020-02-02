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

import java.math.BigInteger;
import java.util.List;

/**
 *
 */
public final class BitFunction extends FixedSignatureFunction {

	public BitFunction() {
		super(ImmutableList.of(
			ProcessedDataType.Integer.INSTANCE
		));
	}

	@Override
	public @NotNull String getName() {
		return "bit";
	}

	@NotNull
	@Override
	protected ProcessedDataType internalCheckType(@NotNull List<ProcessedExpression> arguments, ProcessingSidekick sidekick) {
		ConstantValue value = arguments.get(0).evaluateFormallyConstant(new ProcessedExpression.FormallyConstantEvaluationContext(sidekick));
		BigInteger integerValue = value.convertToInteger();
		if (integerValue == null) {
			return ProcessedDataType.Unknown.INSTANCE;
		}
		if (integerValue.equals(BigInteger.ZERO) || integerValue.equals(BigInteger.ONE)) {
			return ProcessedDataType.Bit.INSTANCE;
		} else {
			sidekick.onError(arguments.get(0), "bit value must be 0 or 1 but is " + integerValue);
			return ProcessedDataType.Unknown.INSTANCE;
		}
	}

	@NotNull
	@Override
	public ConstantValue applyToConstantValues(@NotNull CmLinked errorSource, @NotNull List<ConstantValue> arguments, @NotNull ProcessedExpression.FormallyConstantEvaluationContext context) {
		BigInteger integerValue = arguments.get(0).convertToInteger();
		if (integerValue == null) {
			return ConstantValue.Unknown.INSTANCE;
		}
		if (integerValue.equals(BigInteger.ZERO)) {
			return new ConstantValue.Bit(false);
		} else if (integerValue.equals(BigInteger.ONE)) {
			return new ConstantValue.Bit(true);
		} else {
			return ConstantValue.Unknown.INSTANCE;
		}
	}

}
