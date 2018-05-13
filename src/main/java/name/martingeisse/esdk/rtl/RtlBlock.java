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
public abstract class RtlBlock extends RtlItem {

	private final List<RtlProceduralSignal> proceduralSignals = new ArrayList<>();

	public RtlBlock(RtlDesign design) {
		super(design);
		design.registerBlock(this);
	}

	void registerProceduralSignal(RtlProceduralSignal proceduralSignal) {
		proceduralSignals.add(proceduralSignal);
	}

}
