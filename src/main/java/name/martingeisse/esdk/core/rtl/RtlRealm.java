/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

import java.util.ArrayList;
import java.util.List;

/**
 * An RTL realm is a part ({@link Item}) of a {@link Design} that is expressed on register-transfer level.
 *
 * An RTL realm has its own sub-items of type {@link RtlItem}.
 */
public final class RtlRealm extends Item {

	private final List<RtlPin> pins = new ArrayList<>();
	private final List<RtlClockNetwork> clockNetworks = new ArrayList<>();
	private final List<RtlClockedItem> clockedItems = new ArrayList<>();

	public RtlRealm(Design design) {
		super(design);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerPin(RtlPin.RealmRegistrationKey key, RtlPin pin) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		pins.add(pin);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerClockNetwork(RtlClockNetwork.RealmRegistrationKey key, RtlClockNetwork clockNetwork) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		clockNetworks.add(clockNetwork);
	}

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerClockedItem(RtlClockedItem.RealmRegistrationKey key, RtlClockedItem item) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		clockedItems.add(item);
	}

	public Iterable<RtlPin> getPins() {
		return pins;
	}

	public Iterable<RtlClockNetwork> getClockNetworks() {
		return clockNetworks;
	}

	public Iterable<RtlClockedItem> getClockedItems() {
		return clockedItems;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public RtlClockNetwork createClockNetwork(RtlBitSignal clockSignal) {
		return new RtlClockNetwork(this, clockSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		for (RtlClockedItem item : clockedItems) {
			item.executeInitializer();
		}
		for (RtlClockedItem item : clockedItems) {
			item.updateState();
		}
	}

	void onClockEdge(RtlClockNetwork clockNetwork) {
		for (RtlClockedItem item : clockedItems) {
			if (item.getClockNetwork() == clockNetwork) {
				item.execute();
			}
		}
		for (RtlClockedItem item : clockedItems) {
			if (item.getClockNetwork() == clockNetwork) {
				item.updateState();
			}
		}
	}

}
