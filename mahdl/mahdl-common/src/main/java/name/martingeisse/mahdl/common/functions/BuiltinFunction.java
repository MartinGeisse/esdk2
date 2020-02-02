/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.functions;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmLinked;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 */
public interface BuiltinFunction {

	@NotNull
	String getName();

	@NotNull
	ProcessedDataType checkType(@NotNull CmLinked errorSource,
								@NotNull List<ProcessedExpression> arguments,
								@NotNull ProcessingSidekick sidekick);

	@NotNull
	ConstantValue applyToConstantValues(@NotNull CmLinked errorSource,
										@NotNull List<ConstantValue> arguments,
										@NotNull ProcessedExpression.FormallyConstantEvaluationContext context);

}
