/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.functions.BuiltinFunction;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProcessedFunctionCall extends ProcessedExpression {

	private final BuiltinFunction function;
	private final ImmutableList<ProcessedExpression> arguments;

	public ProcessedFunctionCall(@NotNull CmNode errorSource,
								 @NotNull ProcessedDataType returnType,
								 @NotNull BuiltinFunction function,
								 @NotNull ImmutableList<ProcessedExpression> arguments) {
		super(errorSource, returnType);
		this.function = function;
		this.arguments = arguments;
	}

	@NotNull
	public BuiltinFunction getFunction() {
		return function;
	}

	@NotNull
	public ImmutableList<ProcessedExpression> getArguments() {
		return arguments;
	}

	@Override
	@NotNull
	protected ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		boolean error = false;
		List<ConstantValue> argumentValues = new ArrayList<>();
		for (ProcessedExpression argument : arguments) {
			ConstantValue argumentValue = argument.evaluateFormallyConstant(context);
			argumentValues.add(argumentValue);
			if (argumentValue instanceof ConstantValue.Unknown) {
				error = true;
			}
		}
		if (error) {
			return ConstantValue.Unknown.INSTANCE;
		}
		return function.applyToConstantValues(getErrorSource(), argumentValues, context);
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		List<ProcessedExpression> foldedArguments = new ArrayList<>();
		boolean folded = false;
		for (ProcessedExpression originalArgument : this.arguments) {
			ProcessedExpression foldedArgument = originalArgument.performFolding(sidekick);
			folded |= (foldedArgument != originalArgument);
		}
		return folded ? new ProcessedFunctionCall(getErrorSource(), getDataType(), function, ImmutableList.copyOf(foldedArguments)) : this;
	}

}
