/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.statement;

import name.martingeisse.mahdl.input.cm.CmLinked;
import name.martingeisse.mahdl.input.cm.CmNode;

/**
 *
 */
public abstract class ProcessedStatement implements CmLinked {

	private final CmNode errorSource;

	public ProcessedStatement(CmNode errorSource) {
		this.errorSource = errorSource;
	}

	@Override
	public CmNode getCmNode() {
		return errorSource;
	}

	public CmNode getErrorSource() {
		return errorSource;
	}

	public abstract <R> R visitBranches(BranchVisitor<R> visitor);

}
