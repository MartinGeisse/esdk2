/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;

/**
 * This expression is generated in case of errors.
 */
public final class UnknownExpression extends ProcessedExpression {

	public UnknownExpression(@NotNull CmNode errorSource) {
		super(errorSource, ProcessedDataType.Unknown.INSTANCE);
	}

	@Override
	@NotNull
	protected ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		return ConstantValue.Unknown.INSTANCE;
	}

	@NotNull
	@Override
	protected ProcessedExpression performFolding(@NotNull ProcessingSidekick sidekick) {
		return this;
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		return this;
	}

}
