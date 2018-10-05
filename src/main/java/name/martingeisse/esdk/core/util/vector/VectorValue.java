/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util.vector;

import name.martingeisse.esdk.core.rtl.signal.RtlSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionNesting;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogExpressionWriter;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Represents a bit vector with a specific width.
 * <p>
 * Equality rules: Two vectors are equal if and only if their width and bits are equal. That is, leading zeroes are
 * significant when checking equality. Normally, vectors of different width are not compared at all, so this
 * behavior is defined only for cases such as using vectors as map keys.
 * <p>
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
	 * Returns the value of this vector as an int, using unsigned representation. This vector must be at most 31 bits
	 * wide (otherwise its value cannot be represented as an int).
	 */
	public abstract int getAsUnsignedInt() throws ArithmeticException;

	/**
	 * Returns the value of this vector as a long, using unsigned representation. This vector must be at most 63 bits
	 * wide (otherwise its value cannot be represented as a long).
	 */
	public abstract long getAsUnsignedLong() throws ArithmeticException;

	/**
	 * Returns the value of this vector as an int, mapping the bits one-to-one. This is similar to
	 * {@link #getAsUnsignedInt()} except that it can deal with full 32 bits, using bit 31 as the sign bit of the
	 * returned value. As a result, the return value has no numeric meaning anymore.
	 */
	public abstract int getBitsAsInt() throws IllegalStateException;

	/**
	 * Returns the value of this vector as a long, mapping the bits one-to-one. This is similar to
	 * {@link #getAsUnsignedLong()} except that it can deal with full 64 bits, using bit 63 as the sign bit of the
	 * returned value. As a result, the return value has no numeric meaning anymore.
	 */
	public abstract long getBitsAsLong() throws IllegalStateException;

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

	/**
	 * Expects this vector and the argument vector to be of the same size. Interprets the vectors as unsigned numbers,
	 * multiplies them, truncates the result to the same width and returns it as a vector. (Due to the truncation,
	 * signed / unsigned does not actually make a difference).
	 * <p>
	 * Note that often, the full result of multiplication -- which has up to twice that width -- is needed. In that
	 * case, both vectors must be extended to the full width before multiplication. Signed/unsigned then *does* make
	 * a difference, and is taken into account when extending the inputs (either sign-extending or zero-extending
	 * them).
	 */
	public abstract VectorValue multiply(VectorValue other);

	/**
	 * Selects a single bit.
	 */
	public abstract boolean select(int index);

	/**
	 * Selects a single bit.
	 */
	public abstract boolean select(VectorValue index);

	/**
	 * Selects a range of bits as a vector.
	 */
	public abstract VectorValue select(int from, int to);

	/**
	 * Concatenates this vector (left operand) and the specified bit (right operand).
	 */
	public abstract VectorValue concat(boolean bit);

	/**
	 * Concatenates this vector (left operand) and the specified vector (right operand).
	 */
	public abstract VectorValue concat(VectorValue vector);

	/**
	 * Returns the bitwise NOT of this vector.
	 */
	public abstract VectorValue not();

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-ANDs them.
	 */
	public abstract VectorValue and(VectorValue other);

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-ORs them.
	 */
	public abstract VectorValue or(VectorValue other);

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-XORs them.
	 */
	public abstract VectorValue xor(VectorValue other);

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-XNORs them.
	 */
	public abstract VectorValue xnor(VectorValue other);

	/**
	 * Returns a vector with the same width as this vector, but with the value shifted left by the specified amount.
	 * Shifted-in bits are zero. Shifted-out bits are discarded.
	 * <p>
	 * The amount must not be negative, and must be less than the width of this vector.
	 */
	public abstract VectorValue shiftLeft(int amount);

	/**
	 * Returns a vector with the same width as this vector, but with the value shifted right by the specified amount.
	 * Shifted-in bits are zero. Shifted-out bits are discarded.
	 * <p>
	 * The amount must not be negative, and must be less than the width of this vector.
	 */
	public abstract VectorValue shiftRight(int amount);

	/**
	 * Numerically compares the unsigned meaning of this vector and the specified other vector. Returns -1, 0 or 1
	 * if this vector is less than, equal to, or greater than the argument vector, respectively. The argument must
	 * have the same width as this vector.
	 */
	public abstract int compareUnsigned(VectorValue other);

	public String getVerilogExpression() {
		StringWriter stringWriter = new StringWriter();
		printVerilogExpression(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	public void printVerilogExpression(PrintWriter out) {
		printVerilogExpression(new MyVerilogExpressionWriter(out));
	}

	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(width);
		out.print("'h");
		printDigits(out);
	}

	public String getDigits() {
		StringWriter stringWriter = new StringWriter();
		printDigits(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	public void printDigits(PrintWriter out) {
		printDigits(new MyVerilogExpressionWriter(out));
	}

	public abstract void printDigits(VerilogExpressionWriter out);

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		printVerilogExpression(printWriter);
		printWriter.flush();
		return stringWriter.toString();
	}

	private static final class MyVerilogExpressionWriter implements VerilogExpressionWriter {

		private final PrintWriter out;

		MyVerilogExpressionWriter(PrintWriter out) {
			this.out = out;
		}

		@Override
		public final VerilogExpressionWriter print(String s) {
			out.print(s);
			return this;
		}

		@Override
		public final VerilogExpressionWriter print(int i) {
			out.print(i);
			return this;
		}

		@Override
		public final VerilogExpressionWriter print(char c) {
			out.print(c);
			return this;
		}

		@Override
		public VerilogExpressionWriter print(RtlSignal signal, VerilogExpressionNesting nesting) {
			throw new UnsupportedOperationException();
		}

	}
}
