/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlDomain extends Item {

	private final List<RtlPin> pins = new ArrayList<>();
	private final List<RtlClockNetwork> clockNetworks = new ArrayList<>();
	private final List<RtlClockedBlock> clockedBlocks = new ArrayList<>();

	public RtlDomain(Design design) {
		super(design);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerPin(RtlPin.DomainRegistrationKey key, RtlPin pin) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		pins.add(pin);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerClockNetwork(RtlClockNetwork.DomainRegistrationKey key, RtlClockNetwork clockNetwork) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		clockNetworks.add(clockNetwork);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerBlock(RtlClockedBlock.DomainRegistrationKey key, RtlClockedBlock block) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		clockedBlocks.add(block);
	}

	public Iterable<RtlPin> getPins() {
		return pins;
	}

	public Iterable<RtlClockNetwork> getClockNetworks() {
		return clockNetworks;
	}

	public Iterable<RtlClockedBlock> getClockedBlocks() {
		return clockedBlocks;
	}

	public RtlClockNetwork createClockNetwork(RtlBitSignal clockSignal) {
		return new RtlClockNetwork(this, clockSignal);
	}

	@Override
	protected void initializeSimulation() {
		if (!pins.isEmpty()) {
			throw new IllegalStateException("cannot use an RtlDomain with pins in simulation");
		}
	}

	public void fireClockEdge(RtlClockNetwork clockNetwork) {
		fire(() -> onClockEdge(clockNetwork), 0);
	}

	private void onClockEdge(RtlClockNetwork clockNetwork) {
		for (RtlClockedBlock block : clockedBlocks) {
			if (block.getClockNetwork() == clockNetwork) {
				block.execute();
			}
		}
		List<RtlProceduralSignal> changedSignals = new ArrayList<>();
		for (RtlClockedBlock block : clockedBlocks) {
			if (block.getClockNetwork() == clockNetwork) {
				block.updateProceduralSignals(changedSignals);
			}
		}
	}

}
