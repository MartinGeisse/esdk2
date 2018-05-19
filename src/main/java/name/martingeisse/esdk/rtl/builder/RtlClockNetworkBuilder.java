/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.builder;

import name.martingeisse.esdk.rtl.RtlClockNetwork;
import name.martingeisse.esdk.rtl.RtlClockedBlock;
import name.martingeisse.esdk.rtl.RtlDesign;

/**
 *
 */
public class RtlClockNetworkBuilder {

	private final RtlClockNetwork clockNetwork;

	public RtlClockNetworkBuilder(RtlDesign design) {
		this(new RtlClockNetwork(design));
	}

	public RtlClockNetworkBuilder(RtlClockNetwork clockNetwork) {
		this.clockNetwork = clockNetwork;
	}

	public RtlClockedBlockBuilder createBlock() {
		return new RtlClockedBlockBuilder(new RtlClockedBlock(clockNetwork.getDesign(), clockNetwork));
	}

}
