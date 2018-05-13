/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public abstract class RtlProceduralSignal extends RtlItem implements RtlSignal {

	private final RtlBlock block;

	public RtlProceduralSignal(RtlDesign design, RtlBlock block) {
		super(design);
		checkSameDesign(block);
		this.block = block;
		block.registerProceduralSignal(this);
	}

	public RtlBlock getBlock() {
		return block;
	}

}
