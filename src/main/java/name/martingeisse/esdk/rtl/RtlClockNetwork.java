/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import name.martingeisse.esdk.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.rtl.pin.RtlPin;
import name.martingeisse.esdk.rtl.signal.RtlBitSignal;

/**
 *
 */
public final class RtlClockNetwork extends RtlItem {

	private final RtlBitSignal clockSignal;

	public RtlClockNetwork(RtlDesign design, RtlBitSignal clockSignal) {
		super(design);
		checkSameDesign(clockSignal);
		this.clockSignal = clockSignal;

		DesignRegistrationKey key = new DesignRegistrationKey();
		design.registerClockNetwork(key, this);
		key.valid = false;
	}

	public RtlBitSignal getClockSignal() {
		return clockSignal;
	}

	public RtlClockedBlock createBlock() {
		return new RtlClockedBlock(getDesign(), this);
	}

	/**
	 * This class is used to ensure that {@link RtlDesign#registerPin(RtlPin.DesignRegistrationKey, RtlPin)} isn't called except through the
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
