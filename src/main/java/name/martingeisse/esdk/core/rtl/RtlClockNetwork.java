/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.pin.RtlPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;

/**
 * A real clock network (after synthesis) reacts to real clock edges, i.e. 0-to-1 transitions of the clock signal. In
 * simulation, on the other hand, clock edges must be simulated by calling {@link #fireClockEdge()} since the
 * simulation core doesn't recognize or even simulate asynchronous signal edges. Therefore, simulation of a clock
 * network ignores the clock signal. This shouldn't be a problem in synchronous systems but means you can't use dirty
 * asynchronous tricks such as manually generated clock signals.
 */
public final class RtlClockNetwork extends RtlItem {

	private final RtlBitSignal clockSignal;

	public RtlClockNetwork(RtlRealm realm, RtlBitSignal clockSignal) {
		super(realm);
		this.clockSignal = checkSameRealm(clockSignal);

		RealmRegistrationKey key = new RealmRegistrationKey();
		realm.registerClockNetwork(key, this);
		key.valid = false;
	}

	public RtlBitSignal getClockSignal() {
		return clockSignal;
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

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public RtlClockedBlock createBlock() {
		return new RtlClockedBlock(getRealm(), this);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	public void fireClockEdge() {
		fire(() -> {
			getRealm().onClockEdge(this);
		}, 0);
	}

}
