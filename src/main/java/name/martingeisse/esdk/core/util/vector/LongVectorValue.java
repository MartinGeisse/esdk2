/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util.vector;

import name.martingeisse.esdk.core.rtl.verilog.VerilogExpressionWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public final class LongVectorValue extends VectorValue {

	private final long value;

	LongVectorValue(int width, long value) {
		super(width);
		if (width > 64) {
			throw new IllegalArgumentException("this class does not support widths greater than 64, was: " + width);
		}
		if (width < 64) {
			long mask = ((1L << width) - 1);
			if ((value & mask) != value) {
				throw new IllegalArgumentException("value " + value + " has more than " + width + " bits");
			}
		}
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LongVectorValue) {
			LongVectorValue other = (LongVectorValue) obj;
			return getWidth() == other.getWidth() && value == other.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getWidth()).append(value).toHashCode();
	}

	@Override
	public int getAsUnsignedInt() throws ArithmeticException {
		if (getWidth() > 31) {
			throw new ArithmeticException("cannot convert a vector of width " + getWidth() + " to int");
		}
		return (int) value;
	}

	@Override
	public long getAsUnsignedLong() throws ArithmeticException {
		if (getWidth() > 63) {
			throw new ArithmeticException("cannot convert a vector of width " + getWidth() + " to long");
		}
		return value;
	}

	@Override
	public int getBitsAsInt() throws IllegalStateException {
		if (getWidth() > 32) {
			throw new IllegalStateException("cannot return the bits of a vector of width " + getWidth() + " as int");
		}
		return (int) value;
	}

	@Override
	public long getBitsAsLong() {
		return value;
	}

	private long expectSameWidth(VectorValue other) {
		if (getWidth() != other.getWidth()) {
			throw new IllegalArgumentException("expected a vector of same width as this (" + getWidth() + "), got " + other.getWidth());
		}
		return ((LongVectorValue) other).value;
	}

	private LongVectorValue truncate(long result) {
		return truncate(result, getWidth());
	}

	private static LongVectorValue truncate(long result, int width) {
		if (width < 64) {
			long truncated = result & ((1L << width) - 1);
			return new LongVectorValue(width, truncated);
		} else {
			return new LongVectorValue(width, result);
		}
	}

	@Override
	public VectorValue add(VectorValue other) {
		return truncate(value + expectSameWidth(other));
	}

	@Override
	public VectorValue subtract(VectorValue other) {
		return truncate(value - expectSameWidth(other));
	}

	@Override
	public VectorValue multiply(VectorValue other) {
		return truncate(value * expectSameWidth(other));
	}

	@Override
	public boolean select(int index) {
		int width = getWidth();
		if (index < 0 || index >= width) {
			throw new IllegalArgumentException("invalid index " + index + " for width " + width);
		}
		return ((value >> index) & 1) != 0;
	}

	@Override
	public boolean select(VectorValue index) {
		int indexWidth = index.getWidth();
		if (indexWidth > 31 || (1 << indexWidth > getWidth())) {
			throw new IllegalArgumentException("index width " + indexWidth + " is too wide for vector width " + getWidth());
		}
		return select(index.getAsUnsignedInt());
	}

	@Override
	public VectorValue select(int from, int to) {
		if (to < 0 || from < to || from >= getWidth()) {
			throw new IllegalArgumentException("invalid range [" + from + " .. " + to + "] for width " + getWidth());
		}
		int selectedWidth = from - to + 1;
		return truncate(value >> to, selectedWidth);
	}

	@Override
	public VectorValue concat(boolean bit) {
		return new LongVectorValue(getWidth() + 1, (value << 1) | (bit ? 1 : 0));
	}

	@Override
	public VectorValue concat(VectorValue vector) {
		int otherWidth = vector.getWidth();
		return new LongVectorValue(getWidth() + otherWidth, value << otherWidth | vector.getBitsAsLong());
	}

	@Override
	public VectorValue not() {
		return truncate(~value);
	}

	@Override
	public VectorValue and(VectorValue other) {
		return new LongVectorValue(getWidth(), value & expectSameWidth(other));
	}

	@Override
	public VectorValue or(VectorValue other) {
		return new LongVectorValue(getWidth(), value | expectSameWidth(other));
	}

	@Override
	public VectorValue xor(VectorValue other) {
		return new LongVectorValue(getWidth(), value ^ expectSameWidth(other));
	}

	@Override
	public VectorValue xnor(VectorValue other) {
		return new LongVectorValue(getWidth(), ~(value ^ expectSameWidth(other)));
	}

	@Override
	public VectorValue shiftLeft(int amount) {
		if (amount < 0 || amount >= getWidth()) {
			throw new IllegalArgumentException("invalid shift amount " + amount + " for width " + getWidth());
		}
		return truncate(value << amount);
	}

	@Override
	public VectorValue shiftRight(int amount) {
		if (amount < 0 || amount >= getWidth()) {
			throw new IllegalArgumentException("invalid shift amount " + amount + " for width " + getWidth());
		}
		return new LongVectorValue(getWidth(), value >>> amount);
	}

	@Override
	public int compareUnsigned(VectorValue other) {
		return Long.compareUnsigned(value, expectSameWidth(other));
	}

	protected void printDigits(VerilogExpressionWriter out) {
		int width = getWidth();
		String zeros = StringUtils.repeat('0', width);
		String digits = Long.toString(value, 16);
		out.print((zeros + digits).substring(0, width));
	}

}
