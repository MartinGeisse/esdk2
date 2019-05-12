/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal.connector;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSampler;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;

/**
 * Combines the functionality of {@link RtlBitSignalConnector} and {@link RtlBitSampler} since having to use those
 * two classes together (which is a common case) does not really simplify the implementation of a custom item.
 */
public final class RtlBitSignalConnectorSampler extends RtlClockedItem {

	private RtlBitSignal connected;
	private boolean sample;

	public RtlBitSignalConnectorSampler(RtlClockNetwork clockNetwork) {
		super(clockNetwork);
	}

	public RtlBitSignal getConnected() {
		return connected;
	}

	public void setConnected(RtlBitSignal connected) {
		this.connected = connected;
	}

	public boolean getSample() {
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
