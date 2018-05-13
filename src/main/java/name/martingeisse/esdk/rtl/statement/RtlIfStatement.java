/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlDesign;

/**
 *
 */
public final class RtlIfStatement extends RtlStatement {

	private final RtlStatementSequence thenBranch;
	private final RtlStatementSequence elseBranch;

	public RtlIfStatement(RtlDesign design) {
		super(design);
		thenBranch = new RtlStatementSequence(design);
		elseBranch = new RtlStatementSequence(design);
	}

	public RtlStatementSequence getThenBranch() {
		return thenBranch;
	}

	public RtlStatementSequence getElseBranch() {
		return elseBranch;
	}

}
