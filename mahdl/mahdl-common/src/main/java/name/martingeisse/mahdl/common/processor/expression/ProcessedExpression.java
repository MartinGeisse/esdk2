/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public abstract class ProcessedExpression implements CmLinked {

	@NotNull
	private final CmNode errorSource;

	@NotNull
	private final ProcessedDataType dataType;

	public ProcessedExpression(@NotNull CmNode errorSource, @NotNull ProcessedDataType dataType) {
		this.errorSource = errorSource;
		this.dataType = dataType;
	}

	@Override
	public CmNode getCmNode() {
		return errorSource;
	}

	@NotNull
	public final CmNode getErrorSource() {
		return errorSource;
	}

	@NotNull
	public final ProcessedDataType getDataType() {
		return dataType;
	}

	public final boolean isUnknownType() {
		return dataType.getFamily() == ProcessedDataType.Family.UNKNOWN;
	}

	public final ConstantValue evaluateFormallyConstant(FormallyConstantEvaluationContext context) {
		ConstantValue value = evaluateFormallyConstantInternal(context);
		if (value == null) {
			return context.evaluationInconsistency(this, "evaluating this expression as formally constant returned null");
		} else if (value instanceof ConstantValue.Unknown) {
			return value;
		} else if (!value.getDataType().equals(dataType)) {
			return context.evaluationInconsistency(this, "evaluating this expression as formally constant returned a value of type " +
				value.getDataType() + ", but the expression type was " + dataType);
		} else {
			return value;
		}
	}

	@NotNull
	protected abstract ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context);

	/**
	 * Folding is similar to formally-constant evaluation. If a non-constant (sub-)expression is found, folding the
	 * whole expression fails but other sub-expressions may still be folded. On the other hand, constant evaluation
	 * cannot just try to fold and return the result because if a non-constant sub-expression is found, we want to
	 * report the location of that error as exact as possible.
	 */
	@NotNull
	protected ProcessedExpression performFolding(@NotNull ProcessingSidekick sidekick) {
		ProcessedExpression.FormallyConstantEvaluationContext context = new ProcessedExpression.FormallyConstantEvaluationContext(sidekick) {

			@Override
			@NotNull
			public ConstantValue.Unknown notConstant(@NotNull CmLinked errorSource) {
				throw new NotConstantException();
			}

			@Override
			@NotNull
			public ConstantValue.Unknown notConstant(@NotNull ProcessedExpression errorSource) {
				throw new NotConstantException();

			}
		};
		ConstantValue value;
		try {
			value = evaluateFormallyConstant(context);
		} catch (NotConstantException e) {
			return performSubFolding(sidekick);
		}
		return new ProcessedConstantExpression(errorSource, value);
	}

	@NotNull
	protected abstract ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick);

	private static class NotConstantException extends RuntimeException {
	}

	public static class FormallyConstantEvaluationContext {

		private final ProcessingSidekick sidekick;

		public FormallyConstantEvaluationContext(@NotNull ProcessingSidekick sidekick) {
			this.sidekick = sidekick;
		}

		@NotNull
		public ProcessingSidekick getSidekick() {
			return sidekick;
		}

		@NotNull
		public ConstantValue.Unknown error(@NotNull CmLinked errorSource, @NotNull String message) {
			sidekick.onError(errorSource, message);
			return ConstantValue.Unknown.INSTANCE;
		}

		@NotNull
		public ConstantValue.Unknown error(@NotNull ProcessedExpression errorSource, @NotNull String message) {
			return error(errorSource.getErrorSource(), message);
		}

		@NotNull
		public ConstantValue.Unknown notConstant(@NotNull CmLinked errorSource) {
			return error(errorSource, "expected a formally constant expression");
		}

		@NotNull
		public ConstantValue.Unknown notConstant(@NotNull ProcessedExpression errorSource) {
			return notConstant(errorSource.getErrorSource());
		}

		@NotNull
		public ConstantValue.Unknown evaluationInconsistency(@NotNull CmLinked errorSource, @NotNull String message) {
			return error(errorSource, "internal error: detected an inconsistency between static type check and constant evaluation" +
				(message == null ? "" : (": " + message)));
		}

		@NotNull
		public ConstantValue.Unknown evaluationInconsistency(@NotNull ProcessedExpression errorSource, @NotNull String message) {
			return evaluationInconsistency(errorSource.getErrorSource(), message);
		}

		@NotNull
		public ConstantValue.Unknown evaluationInconsistency(@NotNull CmLinked errorSource) {
			return evaluationInconsistency(errorSource, null);
		}

		@NotNull
		public ConstantValue.Unknown evaluationInconsistency(@NotNull ProcessedExpression errorSource) {
			return evaluationInconsistency(errorSource.getErrorSource());
		}

	}

}
