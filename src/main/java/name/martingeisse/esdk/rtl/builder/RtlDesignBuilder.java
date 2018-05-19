/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.builder;

import name.martingeisse.esdk.rtl.RtlAsynchronousBlock;
import name.martingeisse.esdk.rtl.RtlClockNetwork;
import name.martingeisse.esdk.rtl.RtlDesign;

/**
 *
 */
public class RtlDesignBuilder {

	private final RtlDesign design = new RtlDesign();

	public RtlDesign getDesign() {
		return design;
	}

	public RtlClockNetworkBuilder createClockNetwork() {
		return new RtlClockNetworkBuilder(new RtlClockNetwork(design));
	}

	public RtlAsynchronousBlockBuilder createAsynchronousBlock() {
		return new RtlAsynchronousBlockBuilder(new RtlAsynchronousBlock(design));
	}

}
