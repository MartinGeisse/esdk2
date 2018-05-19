/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.builder;

import name.martingeisse.esdk.rtl.RtlAsynchronousBlock;

/**
 *
 */
public class RtlAsynchronousBlockBuilder extends RtlBlockBuilder {

	private final RtlAsynchronousBlock block;

	public RtlAsynchronousBlockBuilder(RtlAsynchronousBlock block) {
		super(block);
		this.block = block;
	}

	@Override
	public RtlAsynchronousBlock getBlock() {
		return block;
	}

}
