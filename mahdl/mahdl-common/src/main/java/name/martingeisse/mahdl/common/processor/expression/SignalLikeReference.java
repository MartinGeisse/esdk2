/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.definition.Constant;
import name.martingeisse.mahdl.common.processor.definition.SignalLike;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class SignalLikeReference extends ProcessedExpression {

	private final SignalLike definition;

	public SignalLikeReference(@NotNull CmNode errorSource, @NotNull SignalLike definition) {
		super(errorSource, definition.getProcessedDataType());
		this.definition = definition;
	}

	@NotNull
	public SignalLike getDefinition() {
		return definition;
	}

	@Override
	@NotNull
	public ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		ConstantValue constant = getConstant();
		return constant == null ? context.notConstant(this) : constant;
	}

	@NotNull
	@Override
	protected ProcessedExpression performFolding(@NotNull ProcessingSidekick sidekick) {
		ConstantValue constant = getConstant();
		return constant == null ? this : new ProcessedConstantExpression(getErrorSource(), constant);
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		throw new UnsupportedOperationException("should never call this method implementation");
	}

	private ConstantValue getConstant() {
		if (definition instanceof Constant) {
			ConstantValue value = ((Constant) definition).getValue();
			if (value == null) {
				return ConstantValue.Unknown.INSTANCE;
			}
			return value;
		} else {
			return null;
		}
	}

}
