/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.model.Item;

/**
 *
 */
public abstract class RtlItem extends Item implements RtlItemOwned {

	private final RtlRealm realm;

	public RtlItem(RtlRealm realm) {
		super(realm.getDesign());
		this.realm = realm;
	}

	public final RtlRealm getRealm() {
		return realm;
	}

	protected final <T extends RtlItemOwned> T checkSameRealm(T itemOwned) {
		RtlItem item = itemOwned.getRtlItem();
		if (item.getRealm() != realm) {
			String argumentDescription = (item == itemOwned) ?
				("item (" + item + ")") : ("object (" + itemOwned + " from item " + item + ")");
			throw new IllegalArgumentException("the specified " + argumentDescription + " is not part of the same realm as this item (" + this + ")");
		}
		return itemOwned;
	}

	@Override
	public final RtlItem getRtlItem() {
		return this;
	}

}
