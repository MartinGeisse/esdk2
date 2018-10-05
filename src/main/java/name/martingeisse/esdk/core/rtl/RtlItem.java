/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl;

import name.martingeisse.esdk.core.model.Item;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SynthesisNotSupportedException;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;

/**
 * TODO: There is no useful way to generalize how the RTL code is built. It is just a huge pile of workarounds for
 * Verilog quirks. Solution:
 * (1) Enforce that RTL classes are extended only in a very controlled way.
 * (2) Define what is supported, and implement that in the simplest way possible (this includes memories)
 * (3) Remove all support for any customization in the RTL classes except as allowed in (1).
 * (4) Clean up Verilog generation and consider allowing customization there.
 */
public abstract class RtlItem extends Item implements RtlItemOwned {

	private final RtlRealm realm;

	public RtlItem(RtlRealm realm) {
		super(realm.getDesign());
		this.realm = realm;
		realm.registerItem(this);
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

	public abstract VerilogContribution getVerilogContribution();

	public SynthesisNotSupportedException newSynthesisNotSupportedException() {
		return new SynthesisNotSupportedException("synthesis not supported for " + this);
	}

}
