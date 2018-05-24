/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

import java.util.BitSet;

/**
 *
 */
public final class RtlVectorConstant extends RtlItem implements RtlVectorSignal {

	private final int width;
	private final BitSet value;

	public RtlVectorConstant(RtlDesign design, int width, BitSet value) {
		super(design);
		if (value.length() > width) {
			throw new IllegalArgumentException("vector constant value contains 1-bits outside its width");
		}
		this.width = width;
		this.value = (BitSet) value.clone();
	}


	public static RtlVectorConstant from(RtlDesign design, int width, int value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (value > 0) {
			if ((value & 1) != 0) {
				bits.set(index);
			}
			value >>= 1;
			index++;
		}
		return new RtlVectorConstant(design, width, bits);
	}

	@Override
	public int getWidth() {
		return width;
	}

	public BitSet getValue() {
		return (BitSet) value.clone();
	}

}
