/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.builder;

import name.martingeisse.esdk.rtl.statement.RtlStatementSequence;

/**
 *
 */
public class RtlStatementSequenceBuilder {

	private final RtlStatementSequence statementSequence;

	public RtlStatementSequenceBuilder(RtlStatementSequence statementSequence) {
		this.statementSequence = statementSequence;
	}

	public RtlStatementSequence getStatementSequence() {
		return statementSequence;
	}

}
