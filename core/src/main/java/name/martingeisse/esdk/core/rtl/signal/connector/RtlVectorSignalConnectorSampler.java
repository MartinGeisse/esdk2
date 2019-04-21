/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.connector;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSampler;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSampler;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 * Combines the functionality of {@link RtlVectorSignalConnector} and {@link RtlVectorSampler} since having to use those
 * two classes together (which is a common case) does not really simplify the implementation of a custom item.
 */
public final class RtlVectorSignalConnectorSampler extends RtlClockedItem {

	private final int width;
	private RtlVectorSignal connected;
	private VectorValue sample;

	public RtlVectorSignalConnectorSampler(RtlClockNetwork clockNetwork, int width) {
		super(clockNetwork);
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public RtlVectorSignal getConnected() {
		return connected;
	}

	public void setConnected(RtlVectorSignal connected) {
		if (connected.getWidth() != width) {
			throw new IllegalArgumentException("wrong signal width for connected signal: " +
				connected.getWidth() + " (should be " + width + ")");
		}
		this.connected = connected;
	}

	public VectorValue getSample() {
		return sample;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		if (connected == null) {
			throw new IllegalStateException("no connected signal");
		}
		sample = VectorValue.ofUnsigned(getWidth(), 0);
	}

	@Override
	public void computeNextState() {
		sample = connected.getValue();
	}

	@Override
	public void updateState() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
