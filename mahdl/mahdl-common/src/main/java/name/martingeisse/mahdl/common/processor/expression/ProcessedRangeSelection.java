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
public final class ProcessedRangeSelection extends ProcessedExpression {

	private final ProcessedExpression container;
	private final int fromIndex;
	private final int toIndex;

	public ProcessedRangeSelection(@NotNull CmNode errorSource,
								   @NotNull ProcessedDataType dataType,
								   @NotNull ProcessedExpression container,
								   int fromIndex,
								   int toIndex) throws TypeErrorException {
		super(errorSource, dataType);
		if (!(container.getDataType() instanceof ProcessedDataType.Vector)) {
			throw new TypeErrorException("container has type " + container.getDataType());
		}
		int containerSize = ((ProcessedDataType.Vector) container.getDataType()).getSize();
		if (toIndex < 0 || fromIndex < toIndex || fromIndex >= containerSize) {
			throw new TypeErrorException("invalid range: " + fromIndex + ":" + toIndex + " for container size " + containerSize);
		}
		this.container = container;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}

	@NotNull
	public ProcessedExpression getContainer() {
		return container;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public int getToIndex() {
		return toIndex;
	}

	@Override
	@NotNull
	protected ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		return container.evaluateFormallyConstant(context).selectRange(fromIndex, toIndex);
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		ProcessedExpression container = this.container.performFolding(sidekick);
		if (container != this.container) {
			try {
				return new ProcessedRangeSelection(getErrorSource(), getDataType(), container, fromIndex, toIndex);
			} catch (TypeErrorException e) {
				sidekick.onError(getErrorSource(), "internal type error during folding of range selection");
				return this;
			}
		} else {
			return this;
		}
	}

}
