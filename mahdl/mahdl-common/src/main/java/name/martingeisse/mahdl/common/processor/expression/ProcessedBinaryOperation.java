/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class ProcessedBinaryOperation extends ProcessedExpression {

	private final ProcessedExpression leftOperand;
	private final ProcessedExpression rightOperand;
	private final ProcessedBinaryOperator operator;

	public ProcessedBinaryOperation(@NotNull CmNode errorSource,
									@NotNull ProcessedExpression leftOperand,
									@NotNull ProcessedExpression rightOperand,
									@NotNull ProcessedBinaryOperator operator) throws TypeErrorException {
		super(errorSource, operator.checkTypes(leftOperand.getDataType(), rightOperand.getDataType()));
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.operator = operator;
	}

	@NotNull
	public ProcessedExpression getLeftOperand() {
		return leftOperand;
	}

	@NotNull
	public ProcessedExpression getRightOperand() {
		return rightOperand;
	}

	@NotNull
	public ProcessedBinaryOperator getOperator() {
		return operator;
	}

	@Override
	@NotNull
	protected ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		ConstantValue leftOperandValue = leftOperand.evaluateFormallyConstant(context);
		ConstantValue rightOperandValue = rightOperand.evaluateFormallyConstant(context);
		if (leftOperandValue instanceof ConstantValue.Unknown) {
			return leftOperandValue;
		} else if (rightOperandValue instanceof ConstantValue.Unknown) {
			return rightOperandValue;
		} else {
			return operator.evaluateFormallyConstantInternal(leftOperandValue, rightOperandValue, context, getErrorSource());
		}
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		ProcessedExpression leftOperand = this.leftOperand.performFolding(sidekick);
		ProcessedExpression rightOperand = this.rightOperand.performFolding(sidekick);
		if (leftOperand != this.leftOperand || rightOperand != this.rightOperand) {
			try {
				return new ProcessedBinaryOperation(getErrorSource(), leftOperand, rightOperand, operator);
			} catch (TypeErrorException e) {
				sidekick.onError(getErrorSource(), "internal type error during folding of binary operation");
				return this;
			}
		} else {
			return this;
		}
	}

}
