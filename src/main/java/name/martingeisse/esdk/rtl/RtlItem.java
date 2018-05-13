/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public abstract class RtlItem {

	private final RtlDesign design;

	public RtlItem(RtlDesign design) {
		this.design = design;
	}

	public final RtlDesign getDesign() {
		return design;
	}

	protected final void checkSameDesign(RtlItem item) {
		if (item.getDesign() != design) {
			throw new IllegalArgumentException("the specified item (" + item + ") is not part of the same design as this item (" + this + ")");
		}
	}

}
