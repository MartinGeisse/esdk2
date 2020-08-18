/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

/**
 *
 */
public abstract class RtlClockedItem extends RtlItem {

	private final RtlClockNetwork clockNetwork;

	public RtlClockedItem(RtlClockNetwork clockNetwork) {
		super(clockNetwork.getRealm());
		this.clockNetwork = clockNetwork;

		RealmRegistrationKey key = new RealmRegistrationKey();
		getRealm().registerClockedItem(key, this);
		key.valid = false;
	}

	/**
	 * This class is used to ensure that {@link RtlRealm#registerClockedItem(RealmRegistrationKey, RtlClockedItem)}
	 * isn't called except through the {@link RtlClockedItem} constructor.
	 */
	public static final class RealmRegistrationKey {

		private boolean valid = true;

		private RealmRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

	public RtlClockNetwork getClockNetwork() {
		return clockNetwork;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * This method computes the next state of this item based on its current state and the signals provided by other
	 * items. It must not cause changes to any signals, or to its internal state, or to the state of any other item.
	 * <p>
	 * In RTL terms, this method runs between active clock edges and causes the register input signals to stabilize
	 * to their final values.
	 */
	public abstract void computeNextState();

	/**
	 * Updates the current state of this item based on the next state computed by {@link #computeNextState()}. This
	 * method causes signals to change values. It must not obtain the values of any signals or call getters of other
	 * items since those may or may not have changed their value already. It must not change the state of other
	 * items that trigger on the same clock edge since the order in which effects happen is undefined.
	 * <p>
	 * In RTL terms, this method runs at an active clock edge and causes all registers to load a new value.
	 */
	public abstract void updateState();

}
