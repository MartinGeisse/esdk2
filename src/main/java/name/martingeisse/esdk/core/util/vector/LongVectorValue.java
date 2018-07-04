/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util.vector;

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

	private long expectSameWidth(VectorValue other) {
		if (getWidth() != other.getWidth()) {
			throw new IllegalArgumentException("expected a vector of same width as this (" + getWidth() + "), got " + other.getWidth());
		}
		return ((LongVectorValue)other).value;
	}

	private LongVectorValue truncate(long result) {
		int width = getWidth();
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

}
