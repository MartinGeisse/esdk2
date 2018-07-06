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

	public RtlClockNetwork(RtlRegion region, RtlBitSignal clockSignal) {
		super(region);
		checkSameRegion(clockSignal);
		this.clockSignal = clockSignal;

		RegionRegistrationKey key = new RegionRegistrationKey();
		region.registerClockNetwork(key, this);
		key.valid = false;
	}

	public RtlBitSignal getClockSignal() {
		return clockSignal;
	}

	public RtlClockedBlock createBlock() {
		return new RtlClockedBlock(getRegion(), this);
	}

	/**
	 * This class is used to ensure that {@link RtlRegion#registerPin(RtlPin.RegionRegistrationKey, RtlPin)} isn't called except through the
	 * {@link RtlPin} constructor.
	 */
	public static final class RegionRegistrationKey {

		private boolean valid = true;

		private RegionRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

}
