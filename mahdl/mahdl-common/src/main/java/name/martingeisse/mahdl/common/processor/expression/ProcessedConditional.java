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
 *
 */
public final class ProcessedConditional extends ProcessedExpression {

	private final ProcessedExpression condition;
	private final ProcessedExpression thenBranch;
	private final ProcessedExpression elseBranch;

	public ProcessedConditional(@NotNull CmNode errorSource,
								@NotNull ProcessedExpression condition,
								@NotNull ProcessedExpression thenBranch,
								@NotNull ProcessedExpression elseBranch) throws TypeErrorException {
		super(errorSource, thenBranch.getDataType());

		if (!(condition.getDataType() instanceof ProcessedDataType.Bit)) {
			throw new TypeErrorException("condition has type " + condition.getDataType());
		}
		if (!thenBranch.getDataType().equals(elseBranch.getDataType())) {
			throw new TypeErrorException("then/else branches have different types " + thenBranch.getDataType() + " and " + elseBranch.getDataType());
		}

		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	@NotNull
	public ProcessedExpression getCondition() {
		return condition;
	}

	@NotNull
	public ProcessedExpression getThenBranch() {
		return thenBranch;
	}

	@NotNull
	public ProcessedExpression getElseBranch() {
		return elseBranch;
	}

	@Override
	protected ConstantValue evaluateFormallyConstantInternal(FormallyConstantEvaluationContext context) {
		// evaluate both branches to detect errors even in the not-taken branch
		ConstantValue conditionValue = condition.evaluateFormallyConstant(context);
		ConstantValue thenValue = thenBranch.evaluateFormallyConstant(context);
		ConstantValue elseValue = elseBranch.evaluateFormallyConstant(context);
		if (conditionValue.isUnknown()) {
			return conditionValue;
		}
		Boolean conditionBoolean = conditionValue.convertToBoolean();
		if (conditionBoolean == null) {
			return context.evaluationInconsistency(this, "cannot get boolean value form condition");
		} else if (conditionBoolean) {
			return thenValue;
		} else {
			return elseValue;
		}
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		ProcessedExpression condition = this.condition.performFolding(sidekick);
		ProcessedExpression thenBranch = this.thenBranch.performFolding(sidekick);
		ProcessedExpression elseBranch = this.elseBranch.performFolding(sidekick);
		if (condition != this.condition || thenBranch != this.thenBranch || elseBranch != this.elseBranch) {
			try {
				return new ProcessedConditional(getErrorSource(), condition, thenBranch, elseBranch);
			} catch (TypeErrorException e) {
				sidekick.onError(getErrorSource(), "internal type error during folding of conditional expression");
				return this;
			}
		} else {
			return this;
		}
	}

}
