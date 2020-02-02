/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.functions;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmLinked;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 */
public abstract class FixedSignatureFunction extends AbstractFunction {

	private final ImmutableList<ProcessedDataType> argumentTypes;

	public FixedSignatureFunction(ImmutableList<ProcessedDataType> argumentTypes) {
		this.argumentTypes = argumentTypes;
	}

	public ImmutableList<ProcessedDataType> getArgumentTypes() {
		return argumentTypes;
	}

	public String getSignatureText() {
		StringBuilder builder = new StringBuilder(getName());
		builder.append('(');
		boolean first = true;
		for (ProcessedDataType expectedType : argumentTypes) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			builder.append(expectedType);
		}
		builder.append(')');
		return builder.toString();
	}

	@NotNull
	@Override
	public ProcessedDataType checkType(@NotNull CmLinked errorSource, @NotNull List<ProcessedExpression> arguments, @NotNull ProcessingSidekick sidekick) {
		if (arguments.size() != argumentTypes.size()) {
			sidekick.onError(errorSource, getSignatureText() + " cannot be invoked with " + arguments.size() + " arguments");
		}
		for (int i = 0; i < Math.min(arguments.size(), argumentTypes.size()); i++) {
			ProcessedExpression argument = arguments.get(i);
			if (!argument.getDataType().equals(argumentTypes.get(i))) {
				sidekick.onError(argument,
					"argument #" + i + " has type " + argument.getDataType() + ", expected " + argumentTypes.get(i));
			}
		}
		return internalCheckType(arguments, sidekick);
	}

	@NotNull
	protected abstract ProcessedDataType internalCheckType(@NotNull List<ProcessedExpression> arguments, ProcessingSidekick sidekick);

}
