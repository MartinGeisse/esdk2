/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.rtl.block.RtlProceduralSignal;

/**
 *
 */
public abstract class RtlClockedItem extends RtlItem {

	private final RtlClockNetwork clockNetwork;

	public RtlClockedItem(RtlRealm realm, RtlClockNetwork clockNetwork) {
		super(realm);
		this.clockNetwork = checkSameRealm(clockNetwork);

		RealmRegistrationKey key = new RealmRegistrationKey();
		realm.registerClockedItem(key, this);
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

	public abstract void executeInitializer();

	public abstract void execute();

	public abstract void updateState();

}
