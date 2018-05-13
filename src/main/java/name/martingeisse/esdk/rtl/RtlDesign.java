/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlDesign {

	private final List<RtlClockNetwork> clockNetworks = new ArrayList<>();
	private final List<RtlBlock> blocks = new ArrayList<>();

	void registerClockNetwork(RtlClockNetwork clockNetwork) {
		clockNetworks.add(clockNetwork);
	}

	void registerBlock(RtlBlock block) {
		blocks.add(block);
	}

}
