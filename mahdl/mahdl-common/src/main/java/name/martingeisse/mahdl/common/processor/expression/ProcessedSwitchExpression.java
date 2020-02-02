/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.statement.ProcessedAssignment;
import name.martingeisse.mahdl.common.processor.statement.ProcessedStatement;
import name.martingeisse.mahdl.common.processor.statement.ProcessedSwitchStatement;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ProcessedSwitchExpression extends ProcessedExpression {

	@NotNull
	private final ProcessedExpression selector;

	@NotNull
	private final ImmutableList<Case> cases;

	@Nullable
	private final ProcessedExpression defaultBranch;

	public ProcessedSwitchExpression(@NotNull CmNode errorSource,
									 @NotNull ProcessedDataType dataType,
									 @NotNull ProcessedExpression selector,
									 @NotNull ImmutableList<Case> cases,
									 @Nullable ProcessedExpression defaultBranch) throws TypeErrorException {
		super(errorSource, dataType);

		for (Case aCase : cases) {
			for (ConstantValue caseSelectorValue : aCase.getSelectorValues()) {
				if (!caseSelectorValue.getDataType().equals(selector.getDataType())) {
					throw new TypeErrorException("case selector value has type " + caseSelectorValue.getDataType() +
						" but selector expression has type " + selector.getDataType());
				}
			}
			if (!aCase.getResultValue().getDataType().equals(dataType)) {
				throw new TypeErrorException("case has result type " + aCase.getResultValue().getDataType() + " but expression has result type " + dataType);
			}
		}
		if (defaultBranch != null && !defaultBranch.getDataType().equals(dataType)) {
			throw new TypeErrorException("default case has result type " + defaultBranch.getDataType() + " but expression has result type " + dataType);
		}

		this.selector = selector;
		this.cases = cases;
		this.defaultBranch = defaultBranch;
	}

	@NotNull
	public ProcessedExpression getSelector() {
		return selector;
	}

	@NotNull
	public ImmutableList<Case> getCases() {
		return cases;
	}

	@Nullable
	public ProcessedExpression getDefaultBranch() {
		return defaultBranch;
	}

	@Override
	@NotNull
	protected ConstantValue evaluateFormallyConstantInternal(@NotNull FormallyConstantEvaluationContext context) {
		ConstantValue selectorValue = selector.evaluateFormallyConstant(context);
		if (selectorValue instanceof ConstantValue.Unknown) {
			return selectorValue;
		}
		for (Case aCase : cases) {
			for (ConstantValue caseSelectorValue : aCase.getSelectorValues()) {
				if (selectorValue.equals(caseSelectorValue)) {
					return aCase.getResultValue().evaluateFormallyConstant(context);
				}
			}
		}
		if (defaultBranch == null) {
			return context.error(this, "constant selector does not match any match value and no default case exists");
		}
		return defaultBranch.evaluateFormallyConstant(context);
	}

	@NotNull
	@Override
	protected ProcessedExpression performSubFolding(@NotNull ProcessingSidekick sidekick) {
		ProcessedExpression selector = this.selector.performFolding(sidekick);
		ProcessedExpression defaultBranch = (this.defaultBranch == null ? null : this.defaultBranch.performFolding(sidekick));
		boolean folded = (selector != this.selector || defaultBranch != this.defaultBranch);
		List<Case> cases = new ArrayList<>();
		for (Case aCase : this.cases) {
			Case foldedCase = aCase.performFolding(sidekick);
			cases.add(foldedCase);
			if (foldedCase != aCase) {
				folded = true;
			}
		}
		try {
			return folded ? new ProcessedSwitchExpression(getErrorSource(), getDataType(), selector, ImmutableList.copyOf(cases), defaultBranch) : this;
		} catch (TypeErrorException e) {
			sidekick.onError(getErrorSource(), "internal type error during folding of switch expression");
			return this;
		}
	}

	@NotNull
	public ProcessedSwitchStatement convertToStatement(@NotNull ProcessedExpression destination) {
		try {
			List<ProcessedSwitchStatement.Case> statementCases = new ArrayList<>();
			for (ProcessedSwitchExpression.Case expressionCase : cases) {
				ProcessedStatement branch = new ProcessedAssignment(getErrorSource(), destination, expressionCase.getResultValue());
				statementCases.add(new ProcessedSwitchStatement.Case(expressionCase.getSelectorValues(), branch));
			}
			ProcessedStatement defaultBranch = new ProcessedAssignment(getErrorSource(), destination, this.defaultBranch);
			return new ProcessedSwitchStatement(getErrorSource(), selector, ImmutableList.copyOf(statementCases), defaultBranch);
		} catch (TypeErrorException e) {
			throw new RuntimeException(e);
		}
	}

	public static final class Case {

		@NotNull
		private final ImmutableList<ConstantValue.Vector> selectorValues;

		@NotNull
		private final ProcessedExpression resultValue;

		public Case(@NotNull ImmutableList<ConstantValue.Vector> selectorValues, @NotNull ProcessedExpression resultValue) {
			this.selectorValues = selectorValues;
			this.resultValue = resultValue;
		}

		@NotNull
		public ImmutableList<ConstantValue.Vector> getSelectorValues() {
			return selectorValues;
		}

		@NotNull
		public ProcessedExpression getResultValue() {
			return resultValue;
		}

		public Case performFolding(ProcessingSidekick sidekick) {
			ProcessedExpression resultValue = this.resultValue.performFolding(sidekick);
			return (resultValue == this.resultValue ? this : new Case(selectorValues, resultValue));
		}

	}

}
