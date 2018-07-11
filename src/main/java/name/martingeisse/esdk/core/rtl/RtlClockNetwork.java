/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlClockNetwork extends RtlItem {

	private final RtlBitSignal clockSignal;

	public RtlClockNetwork(RtlRealm realm, RtlBitSignal clockSignal) {
		super(realm);
		checkSameRealm(clockSignal);
		this.clockSignal = clockSignal;

		RealmRegistrationKey key = new RealmRegistrationKey();
		realm.registerClockNetwork(key, this);
		key.valid = false;
	}

	public RtlBitSignal getClockSignal() {
		return clockSignal;
	}

	public RtlClockedBlock createBlock() {
		return new RtlClockedBlock(getRealm(), this);
	}

	/**
	 * This class is used to ensure that {@link RtlRealm#registerPin(RtlPin.RealmRegistrationKey, RtlPin)} isn't called except through the
	 * {@link RtlPin} constructor.
	 */
	public static final class RealmRegistrationKey {

		private boolean valid = true;

		private RealmRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

}
