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

	@Override
	public boolean compliesWith(VerilogDesignGenerator.VerilogExpressionNesting nesting) {
		return true;
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(width);
		out.print("'h");
		printDigits(0, out);
	}

	private void printDigits(int baseIndex, VerilogExpressionWriter out) {

		// print digits for higher order bits
		if (baseIndex < value.length()) {
			printDigits(baseIndex + 4, out);
		}

		// print digit
		int digitValue =
			(value.get(0) ? 1 : 0) +
			(value.get(1) ? 2 : 0) +
			(value.get(2) ? 4 : 0) +
			(value.get(3) ? 8 : 0);
		int symbol = (digitValue < 10 ? (digitValue + '0') : (digitValue - 10 + 'a'));
		out.print((char)symbol);

	}

}
