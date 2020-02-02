/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.TypeErrorException;
import name.martingeisse.mahdl.input.cm.CmNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public final class ProcessedSwitchStatement extends ProcessedStatement {

	@NotNull
	private final ProcessedExpression selector;

	@NotNull
	private final ImmutableList<Case> cases;

	@Nullable
	private final ProcessedStatement defaultBranch;

	public ProcessedSwitchStatement(@NotNull CmNode errorSource,
									@NotNull ProcessedExpression selector,
									@NotNull ImmutableList<Case> cases,
									@Nullable ProcessedStatement defaultBranch) throws TypeErrorException {
		super(errorSource);
		for (Case aCase : cases) {
			for (ConstantValue caseSelectorValue : aCase.getSelectorValues()) {
				if (!caseSelectorValue.getDataType().equals(selector.getDataType())) {
					throw new TypeErrorException("case selector value has type " + caseSelectorValue.getDataType() +
						" but selector expression has type " + selector.getDataType());
				}
			}
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
	public ProcessedStatement getDefaultBranch() {
		return defaultBranch;
	}

	@Override
	public <R> R visitBranches(BranchVisitor<R> visitor) {
		R result = visitor.visit(defaultBranch);
		for (Case aCase : cases) {
			result = visitor.getBranchOperator().apply(result, visitor.visit(aCase.getBranch()));
		}
		return result;
	}

	public static final class Case {

		@NotNull
		private final ImmutableList<ConstantValue.Vector> selectorValues;

		@NotNull
		private final ProcessedStatement branch;

		public Case(@NotNull ImmutableList<ConstantValue.Vector> selectorValues, @NotNull ProcessedStatement branch) {
			this.selectorValues = selectorValues;
			this.branch = branch;
		}

		@NotNull
		public ImmutableList<ConstantValue.Vector> getSelectorValues() {
			return selectorValues;
		}

		@NotNull
		public ProcessedStatement getBranch() {
			return branch;
		}

	}

}
