/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlClockNetwork extends RtlItem {

	public RtlClockNetwork(RtlDesign design) {
		super(design);
		design.registerClockNetwork(this);
	}

	public RtlClockedBlock createBlock() {
		return new RtlClockedBlock(getDesign(), this);
	}

}
