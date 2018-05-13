/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlBitSignal;
import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlProceduralBitSignal;

/**
 *
 */
public final class RtlProceduralBitSignalAssignment extends RtlProceduralSignalAssignment {

	private final RtlProceduralBitSignal destination;
	private final RtlBitSignal source;

	public RtlProceduralBitSignalAssignment(RtlDesign design, RtlProceduralBitSignal destination, RtlBitSignal source) {
		super(design);
		this.destination = destination;
		this.source = source;
	}

	@Override
	public RtlProceduralBitSignal getDestination() {
		return destination;
	}

	@Override
	public RtlBitSignal getSource() {
		return source;
	}

}
