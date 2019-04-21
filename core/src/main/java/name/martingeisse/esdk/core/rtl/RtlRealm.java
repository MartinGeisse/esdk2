/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.module.RtlModuleInstance;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

import java.util.ArrayList;
import java.util.List;

/**
 * An RTL realm is a part ({@link Item}) of a {@link Design} that is expressed on register-transfer level.
 *
 * An RTL realm has its own sub-items of type {@link RtlItem}.
 *
 * TODO reconsider if RtlRealm is needed. A central registration point for all RtlItems is certainly needed
 * (for running initialization code; for synthesis; ...) but maybe {@link Design} can handle that. Then {@link RtlItem}
 * could be merged into {@link Item} and RtlRealm could be removed.
 */
public final class RtlRealm extends Item {

	private final List<RtlItem> items = new ArrayList<>();
	private final List<RtlPin> pins = new ArrayList<>();
	private final List<RtlClockNetwork> clockNetworks = new ArrayList<>();
	private final List<RtlClockedItem> clockedItems = new ArrayList<>();
	private final List<RtlModuleInstance> moduleInstances = new ArrayList<>();

	public RtlRealm(Design design) {
		super(design);
	}

	void registerItem(RtlItem item) {
		items.add(item);
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

	/**
	 * Non-public API. Do not call. Only marked public because Java forces us to if we want to use packages.
	 */
	public void registerModuleInstance(RtlModuleInstance.RealmRegistrationKey key, RtlModuleInstance item) {
		if (!key.isValid()) {
			throw new IllegalArgumentException("invalid registration key");
		}
		moduleInstances.add(item);
	}

	public Iterable<RtlItem> getItems() {
		return items;
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

	public List<RtlModuleInstance> getModuleInstances() {
		return moduleInstances;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	// TODO synthesis should perform plausibility checks such as if the clock source is constant, to avoid looking for
	// errors in the generated code which is hard to read.
	public RtlClockNetwork createClockNetwork(RtlBitSignal clockSignal) {
		return new RtlClockNetwork(this, clockSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	void onClockEdge(RtlClockNetwork clockNetwork) {
		for (RtlClockedItem item : clockedItems) {
			if (item.getClockNetwork() == clockNetwork) {
				item.computeNextState();
			}
		}
		for (RtlClockedItem item : clockedItems) {
			if (item.getClockNetwork() == clockNetwork) {
				item.updateState();
			}
		}
	}

}
