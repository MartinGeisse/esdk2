/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public abstract class RtlItem implements RtlItemOwned {

	private final RtlDesign design;

	public RtlItem(RtlDesign design) {
		this.design = design;
	}

	public final RtlDesign getDesign() {
		return design;
	}

	protected final void checkSameDesign(RtlItemOwned itemOwned) {
		RtlItem item = itemOwned.getRtlItem();
		if (item.getDesign() != design) {
			String argumentDescription = (item == itemOwned) ?
				("item (" + item + ")") : ("object (" + itemOwned + " from item " + item + ")");
			throw new IllegalArgumentException("the specified " + argumentDescription + " is not part of the same design as this item (" + this + ")");
		}
	}

	@Override
	public final RtlItem getRtlItem() {
		return this;
	}

}
