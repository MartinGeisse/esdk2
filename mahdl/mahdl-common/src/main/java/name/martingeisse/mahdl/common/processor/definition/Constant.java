/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmToken;
import name.martingeisse.mahdl.input.cm.DataType;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public final class Constant extends SignalLike {

	@Nullable
	private ConstantValue value;

	// note: the initializer can be null in case of errors in the code
	public Constant(@NotNull CmToken nameElement,
					@NotNull DataType dataTypeElement,
					@NotNull ProcessedDataType processedDataType,
					@Nullable ExtendedExpression initializer) {
		super(nameElement, dataTypeElement, processedDataType, initializer);
	}

	public void evaluate(@NotNull ProcessedExpression.FormallyConstantEvaluationContext context) {
		if (getProcessedInitializer() == null) {
			this.value = ConstantValue.Unknown.INSTANCE;
		} else {
			this.value = getProcessedInitializer().evaluateFormallyConstant(context);
		}
	}

	@Nullable
	public ConstantValue getValue() {
		return value;
	}

}
