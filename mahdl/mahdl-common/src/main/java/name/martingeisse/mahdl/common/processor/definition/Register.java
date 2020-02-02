/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.definition;

import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
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
public final class Register extends SignalLike {

	@Nullable
	private ConstantValue initializerValue;

	public Register(@NotNull CmToken nameElement,
					@NotNull DataType dataTypeElement,
					@NotNull ProcessedDataType processedDataType,
					@Nullable ExtendedExpression initializer) {
		super(nameElement, dataTypeElement, processedDataType, initializer);
	}

	@Override
	public void processExpressions(@NotNull ExpressionProcessor expressionProcessor) {
		super.processExpressions(expressionProcessor);
		if (getProcessedInitializer() != null) {
			// if there is an initializer then it must be formally constant
			initializerValue = getProcessedInitializer().evaluateFormallyConstant(
				new ProcessedExpression.FormallyConstantEvaluationContext(expressionProcessor.getSidekick()));
		}
	}

	@Nullable
	public ConstantValue getInitializerValue() {
		return initializerValue;
	}

}
