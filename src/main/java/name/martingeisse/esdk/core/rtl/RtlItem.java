/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

/**
 *
 */
public abstract class RtlItem implements RtlItemOwned {

	private final RtlDomain domain;

	public RtlItem(RtlDomain domain) {
		this.domain = domain;
	}

	public final RtlDomain getDomain() {
		return domain;
	}

	protected final void checkSameDesign(RtlItemOwned itemOwned) {
		RtlItem item = itemOwned.getRtlItem();
		if (item.getDomain() != domain) {
			String argumentDescription = (item == itemOwned) ?
				("item (" + item + ")") : ("object (" + itemOwned + " from item " + item + ")");
			throw new IllegalArgumentException("the specified " + argumentDescription + " is not part of the same domain as this item (" + this + ")");
		}
	}

	@Override
	public final RtlItem getRtlItem() {
		return this;
	}

}
