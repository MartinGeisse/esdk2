/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import name.martingeisse.esdk.rtl.block.RtlAsynchronousBlock;
import name.martingeisse.esdk.rtl.block.RtlBlock;
import name.martingeisse.esdk.rtl.pin.RtlPin;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class RtlDesign {

	private final List<RtlPin> pins = new ArrayList<>();
	private final List<RtlClockNetwork> clockNetworks = new ArrayList<>();
	private final List<RtlBlock> blocks = new ArrayList<>();

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerPin(RtlPin.DesignRegistrationKey key, RtlPin pin) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		pins.add(pin);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerClockNetwork(RtlClockNetwork.DesignRegistrationKey key, RtlClockNetwork clockNetwork) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		clockNetworks.add(clockNetwork);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerBlock(RtlBlock.DesignRegistrationKey key, RtlBlock block) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		blocks.add(block);
	}

	public Iterable<RtlPin> getPins() {
		return pins;
	}

	public Iterable<RtlClockNetwork> getClockNetworks() {
		return clockNetworks;
	}

	public Iterable<RtlBlock> getBlocks() {
		return blocks;
	}

	public RtlClockNetwork createClockNetwork() {
		return new RtlClockNetwork(this);
	}

	public RtlAsynchronousBlock createAsynchronousBlock() {
		return new RtlAsynchronousBlock(this);
	}

}
