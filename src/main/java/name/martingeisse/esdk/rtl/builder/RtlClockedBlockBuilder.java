/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.builder;

import name.martingeisse.esdk.rtl.RtlClockedBlock;

/**
 *
 */
public class RtlClockedBlockBuilder extends RtlBlockBuilder {

	private final RtlClockedBlock block;
	private final RtlStatementSequenceBuilder initializerBuilder;

	public RtlClockedBlockBuilder(RtlClockedBlock block) {
		super(block);
		this.block = block;
		this.initializerBuilder = new RtlStatementSequenceBuilder(block.getInitializerStatements());
	}

	@Override
	public RtlClockedBlock getBlock() {
		return block;
	}

	public RtlStatementSequenceBuilder getInitializerBuilder() {
		return initializerBuilder;
	}

}
