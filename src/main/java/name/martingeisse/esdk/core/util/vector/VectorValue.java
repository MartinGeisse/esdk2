/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util.vector;

/**
 * Represents a bit vector with a specific width.
 */
public abstract class VectorValue {

	private final int width;

	/**
	 * Creates a vector value with the specified width and whose bits are the unsigned representation of the specified
	 * value. If the width is greater than 32, then the upper bits will be 0. If the width is less than 32, then the
	 * remaining upper bits of the value argument must be 0.
	 */
	public static VectorValue ofUnsigned(int width, int value) {
		if (width > 64) {
			throw new UnsupportedOperationException("vectors larger than 64 bits not yet implemented");
		}
		return new LongVectorValue(width, value);
	}

	VectorValue(int width) {
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		this.width = width;
	}

	public final int getWidth() {
		return width;
	}

	/**
	 * Expects this vector and the argument vector to be of the same size. Interprets the vectors as unsigned numbers,
	 * adds them, truncates the result to the same width and returns it as a vector. (Due to the truncation,
	 * signed / unsigned does not actually make a difference).
	 */
	public abstract VectorValue add(VectorValue other);

	/**
	 * Expects this vector and the argument vector to be of the same size. Interprets the vectors as unsigned numbers,
	 * subtracts them, truncates the result to the same width and returns it as a vector. (Due to the truncation,
	 * signed / unsigned does not actually make a difference).
	 */
	public abstract VectorValue subtract(VectorValue other);

}
