/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.verilog.PrintWriterVerilogExpressionWriter;
import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.BitSet;

/**
 * Represents a bit vector value -- basically, an immutable {@link BitSet} with an explicit width.
 * <p>
 * Note that there is no corresponding RtlBitValue since we use primitive boolean values instead.
 */
public final class RtlVectorValue {

	private final int width;
	private final BitSet bits;

	/**
	 * Constructor. This clones the bit set to guarantee immutability.
	 */
	public RtlVectorValue(int width, BitSet bits) {
		this(width, bits, true);
	}

	/**
	 * Internal constructor that allows to pass the internal bit set without cloning it. The caller must ensure that
	 * the bit set is not shared to guarantee immutability.
	 */
	RtlVectorValue(int width, BitSet bits, boolean cloneBits) {
		if (bits.length() > width) {
			throw new IllegalArgumentException("vector constant value contains 1-bits outside its width");
		}
		this.width = width;
		if (cloneBits) {
			this.bits = (BitSet) bits.clone();
		} else {
			this.bits = bits;
		}
	}

	public static RtlVectorValue from(int width, int value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (value > 0) {
			if ((value & 1) != 0) {
				bits.set(index);
			}
			value >>= 1;
			index++;
		}
		return new RtlVectorValue(width, bits);
	}

	public int getWidth() {
		return width;
	}

	public BitSet getBits() {
		return (BitSet) bits.clone();
	}

	BitSet getBitsShared() {
		return bits;
	}

	public void printVerilogExpression(PrintWriter out) {
		printVerilogExpression(new PrintWriterVerilogExpressionWriter(out));
	}

	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(width);
		out.print("'h");
		printDigits(0, out);
	}

	private void printDigits(int baseIndex, VerilogExpressionWriter out) {

		// print digits for higher order bits
		if (baseIndex + 4 < bits.length()) {
			printDigits(baseIndex + 4, out);
		}

		// print digit
		int digitValue = (bits.get(0) ? 1 : 0) + (bits.get(1) ? 2 : 0) + (bits.get(2) ? 4 : 0) + (bits.get(3) ? 8 : 0);
		int symbol = (digitValue < 10 ? (digitValue + '0') : (digitValue - 10 + 'a'));
		out.print((char) symbol);

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RtlVectorValue) {
			RtlVectorValue other = (RtlVectorValue)obj;
			return width == other.width && bits.equals(other.bits);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(width).append(bits).toHashCode();
	}

	public BigInteger toUnsignedInteger() {
		TODO
	}

}
