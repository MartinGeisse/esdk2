/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.builder;

import name.martingeisse.esdk.rtl.RtlBlock;
import name.martingeisse.esdk.rtl.statement.RtlStatementSequence;

/**
 *
 */
public class RtlBlockBuilder extends RtlStatementSequenceBuilder {

	private final RtlBlock block;

	public RtlBlockBuilder(RtlBlock block) {
		super(block.getStatements());
		this.block = block;
	}

	public RtlBlock getBlock() {
		return block;
	}

}
