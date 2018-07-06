/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

/**
 *
 */
public abstract class RtlItem implements RtlItemOwned {

	private final RtlRegion region;

	public RtlItem(RtlRegion region) {
		this.region = region;
	}

	public final RtlRegion getRegion() {
		return region;
	}

	protected final void checkSameRegion(RtlItemOwned itemOwned) {
		RtlItem item = itemOwned.getRtlItem();
		if (item.getRegion() != region) {
			String argumentDescription = (item == itemOwned) ?
				("item (" + item + ")") : ("object (" + itemOwned + " from item " + item + ")");
			throw new IllegalArgumentException("the specified " + argumentDescription + " is not part of the same region as this item (" + this + ")");
		}
	}

	@Override
	public final RtlItem getRtlItem() {
		return this;
	}

}
