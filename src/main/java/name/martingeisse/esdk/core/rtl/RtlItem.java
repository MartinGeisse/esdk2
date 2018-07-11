/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

/**
 *
 */
public abstract class RtlItem implements RtlItemOwned {

	private final RtlRealm realm;

	public RtlItem(RtlRealm realm) {
		this.realm = realm;
	}

	public final RtlRealm getRealm() {
		return realm;
	}

	protected final void checkSameRealm(RtlItemOwned itemOwned) {
		RtlItem item = itemOwned.getRtlItem();
		if (item.getRealm() != realm) {
			String argumentDescription = (item == itemOwned) ?
				("item (" + item + ")") : ("object (" + itemOwned + " from item " + item + ")");
			throw new IllegalArgumentException("the specified " + argumentDescription + " is not part of the same realm as this item (" + this + ")");
		}
	}

	@Override
	public final RtlItem getRtlItem() {
		return this;
	}

}
