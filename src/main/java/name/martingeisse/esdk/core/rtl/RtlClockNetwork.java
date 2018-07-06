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

	public RtlClockNetwork(RtlDomain domain, RtlBitSignal clockSignal) {
		super(domain);
		checkSameDesign(clockSignal);
		this.clockSignal = clockSignal;

		DesignRegistrationKey key = new DesignRegistrationKey();
		domain.registerClockNetwork(key, this);
		key.valid = false;
	}

	public RtlBitSignal getClockSignal() {
		return clockSignal;
	}

	public RtlClockedBlock createBlock() {
		return new RtlClockedBlock(getDomain(), this);
	}

	/**
	 * This class is used to ensure that {@link RtlDomain#registerPin(RtlPin.DesignRegistrationKey, RtlPin)} isn't called except through the
	 * {@link RtlPin} constructor.
	 */
	public static final class DesignRegistrationKey {

		private boolean valid = true;

		private DesignRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

}
