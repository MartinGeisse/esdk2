/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlDesign;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlStatementSequence extends RtlStatement {

	private final List<RtlStatement> statements = new ArrayList<>();

	public RtlStatementSequence(RtlDesign design) {
		super(design);
	}

	public void addStatement(RtlStatement statement) {
		checkSameDesign(statement);
		statements.add(statement);
	}

}
