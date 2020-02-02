/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.input.cm.CmNode;

/**
 *
 */
public final class ProcessedBlock extends ProcessedStatement {

	private final ImmutableList<ProcessedStatement> statements;

	public ProcessedBlock(CmNode errorSource, ImmutableList<ProcessedStatement> statements) {
		super(errorSource);
		this.statements = statements;
	}

	public ImmutableList<ProcessedStatement> getStatements() {
		return statements;
	}

	@Override
	public <R> R visitBranches(BranchVisitor<R> visitor) {
		R result = visitor.getEmptyResult();
		for (ProcessedStatement statement : statements) {
			result = visitor.getSequenceOperator().apply(result, visitor.visit(statement));
		}
		return result;
	}

}
