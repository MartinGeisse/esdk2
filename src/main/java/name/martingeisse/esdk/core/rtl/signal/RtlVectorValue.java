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

	public static RtlVectorValue from(int width, BigInteger value) {
		BitSet bits = new BitSet();
		int index = 0;
		while (!value.equals(BigInteger.ZERO)) {
			if (value.testBit(0)) {
				bits.set(index);
			}
			value = value.shiftRight(1);
			index++;
		}
		return new RtlVectorValue(width, bits);
	}

	public static RtlVectorValue zeroes(int width) {
		return new RtlVectorValue(width, new BitSet(), false);
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

	public boolean getBit(int index) {
		if (index < 0 || index >= width) {
			throw new IllegalArgumentException("vector index out of bounds: " + index + " (width: " + width + ")");
		}
		return bits.get(index);
	}

	public RtlVectorValue getRange(int from, int to) {
		if (from < 0 || to < 0 || from >= width || to >= width || from < to) {
			throw new IllegalArgumentException("invalid from/to indices for container width " + width + ": from = " + from + ", to = " + to);
		}
		int resultWidth = from - to + 1;
		BitSet resultBits = new BitSet();
		for (int i = 0; i < resultWidth; i++) {
			resultBits.set(i, bits.get(to + i));
		}
		return new RtlVectorValue(resultWidth, resultBits);
	}



	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RtlVectorValue) {
			RtlVectorValue other = (RtlVectorValue) obj;
			return width == other.width && bits.equals(other.bits);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(width).append(bits).toHashCode();
	}

	/**
	 * Interprets this vector as an unsigned binary integer.
	 */
	public BigInteger convertUnsignedToBigInteger() {
		BigInteger result = BigInteger.ZERO;
		for (int i = 0; i < width; i++) {
			if (bits.get(i)) {
				result = result.add(BigInteger.ONE.shiftLeft(i));
			}
		}
		return result;
	}

	/**
	 * Interprets this vector as an unsigned binary integer. Unlike {@link #convertUnsignedToBigInteger()}, this method
	 * demands that the vector width is at most 31, so the result can be returned as a primitive int value.
	 */
	public int convertUnsignedToSmallInteger() {
		if (width > 31) {
			throw new IllegalStateException("cannot use this method on vector of width " + width + ", at most 31 is allowed");
		}
		int result = 0;
		for (int i = 0; i < width; i++) {
			if (bits.get(i)) {
				result += (1 << i);
			}
		}
		return result;
	}

	/**
	 * Interprets this vector as an unsigned binary integer. Unlike {@link #convertUnsignedToBigInteger()}, this method
	 * demands that the vector width is at most 63, so the result can be returned as a primitive long value.
	 */
	public long convertUnsignedToLongInteger() {
		if (width > 63) {
			throw new IllegalStateException("cannot use this method on vector of width " + width + ", at most 63 is allowed");
		}
		long result = 0;
		for (int i = 0; i < width; i++) {
			if (bits.get(i)) {
				result += (1L << i);
			}
		}
		return result;
	}

	/**
	 * Returns a new vector of the same width as this vector and with all the bits shifted to the left, zeroes shifted
	 * in from the right.
	 * <p>
	 * The amount must be non-negative and less than the width of this vector. This sidesteps the question of whether a
	 * large shift amount is truncated to a small shift amount or if it causes the result to be zero (both cases are
	 * implemented in various systems, and any confusion here can easily cause errors or inefficencies).
	 */
	public RtlVectorValue shiftLeft(int amount) {
		BitSet resultBits = new BitSet(width);
		for (int i = amount; i < width; i++) {
			resultBits.set(i, bits.get(i - amount));
		}
		return new RtlVectorValue(width, resultBits);
	}

	/**
	 * Returns a new vector of the same width as this vector and with all the bits shifted to the right, zeroes shifted
	 * in from the left.
	 * <p>
	 * The amount must be non-negative and less than the width of this vector. This sidesteps the question of whether a
	 * large shift amount is truncated to a small shift amount or if it causes the result to be zero (both cases are
	 * implemented in various systems, and any confusion here can easily cause errors or inefficencies).
	 */
	public RtlVectorValue shiftRight(int amount) {
		BitSet resultBits = new BitSet(width);
		for (int i = amount; i < width; i++) {
			resultBits.set(i - amount, bits.get(i));
		}
		return new RtlVectorValue(width, resultBits);
	}

}
