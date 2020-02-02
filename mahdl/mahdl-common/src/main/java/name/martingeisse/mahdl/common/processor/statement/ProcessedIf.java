/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.statement;

import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.input.cm.CmNode;

/**
 *
 */
public final class ProcessedIf extends ProcessedStatement {

	private final ProcessedExpression condition;
	private final ProcessedStatement thenBranch;
	private final ProcessedStatement elseBranch;

	public ProcessedIf(CmNode errorSource, ProcessedExpression condition, ProcessedStatement thenBranch, ProcessedStatement elseBranch) {
		super(errorSource);
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	public ProcessedExpression getCondition() {
		return condition;
	}

	public ProcessedStatement getThenBranch() {
		return thenBranch;
	}

	public ProcessedStatement getElseBranch() {
		return elseBranch;
	}

	@Override
	public <R> R visitBranches(BranchVisitor<R> visitor) {
		return visitor.getBranchOperator().apply(visitor.visit(thenBranch), visitor.visit(elseBranch));
	}

}
