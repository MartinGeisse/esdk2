/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.util.vector.VectorValue;

/**
 *
 */
public interface RtlVectorSignal extends RtlSignal {

	int getWidth();

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	VectorValue getValue();

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	//
	// selection
	//

	default RtlBitSignal select(RtlVectorSignal index) {
		return new RtlIndexSelection(getRtlItem().getRealm(), this, index);
	}

	default RtlBitSignal select(int index) {
		return new RtlConstantIndexSelection(getRtlItem().getRealm(), this, index);
	}

	default RtlVectorSignal select(int from, int to) {
		return new RtlRangeSelection(getRtlItem().getRealm(), this, from, to);
	}

	//
	// vector operations
	//

	default RtlVectorNotOperation not() {
		return new RtlVectorNotOperation(getRtlItem().getRealm(), this);
	}

	default RtlVectorSignal operation(RtlVectorOperation.Operator operator, RtlVectorSignal rightOperand) {
		return new RtlVectorOperation(getRtlItem().getRealm(), operator, this, rightOperand);
	}

	default RtlVectorSignal operation(RtlVectorOperation.Operator operator, VectorValue rightOperand) {
		return operation(operator, new RtlVectorConstant(getRtlItem().getRealm(), rightOperand));
	}

	default RtlVectorSignal operation(RtlVectorOperation.Operator operator, int rightOperand) {
		return operation(operator, RtlVectorConstant.of(getRtlItem().getRealm(), getWidth(), rightOperand));
	}

	default RtlVectorSignal add(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.ADD, rightOperand);
	}

	default RtlVectorSignal add(VectorValue rightOperand) {
		return operation(RtlVectorOperation.Operator.ADD, rightOperand);
	}

	default RtlVectorSignal add(int rightOperand) {
		return operation(RtlVectorOperation.Operator.ADD, rightOperand);
	}

	default RtlVectorSignal subtract(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default RtlVectorSignal subtract(VectorValue rightOperand) {
		return operation(RtlVectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default RtlVectorSignal subtract(int rightOperand) {
		return operation(RtlVectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default RtlVectorSignal multiply(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default RtlVectorSignal multiply(VectorValue rightOperand) {
		return operation(RtlVectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default RtlVectorSignal multiply(int rightOperand) {
		return operation(RtlVectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default RtlVectorSignal and(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.AND, rightOperand);
	}

	default RtlVectorSignal and(VectorValue rightOperand) {
		return operation(RtlVectorOperation.Operator.AND, rightOperand);
	}

	default RtlVectorSignal and(int rightOperand) {
		return operation(RtlVectorOperation.Operator.AND, rightOperand);
	}

	default RtlVectorSignal or(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.OR, rightOperand);
	}

	default RtlVectorSignal or(VectorValue rightOperand) {
		return operation(RtlVectorOperation.Operator.OR, rightOperand);
	}

	default RtlVectorSignal or(int rightOperand) {
		return operation(RtlVectorOperation.Operator.OR, rightOperand);
	}

	default RtlVectorSignal xor(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.XOR, rightOperand);
	}

	default RtlVectorSignal xor(VectorValue rightOperand) {
		return operation(RtlVectorOperation.Operator.XOR, rightOperand);
	}

	default RtlVectorSignal xor(int rightOperand) {
		return operation(RtlVectorOperation.Operator.XOR, rightOperand);
	}

	//
	// comparisons
	//

	default RtlBitSignal comparison(RtlVectorComparison.Operator operator, RtlVectorSignal rightOperand) {
		return new RtlVectorComparison(getRtlItem().getRealm(), operator, this, rightOperand);
	}

	default RtlBitSignal comparison(RtlVectorComparison.Operator operator, VectorValue rightOperand) {
		return comparison(operator, new RtlVectorConstant(getRtlItem().getRealm(), rightOperand));
	}

	default RtlBitSignal comparison(RtlVectorComparison.Operator operator, int rightOperand) {
		return comparison(operator, RtlVectorConstant.of(getRtlItem().getRealm(), getWidth(), rightOperand));
	}

	default RtlBitSignal compareEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.EQUAL, rightOperand);
	}

	default RtlBitSignal compareEqual(VectorValue rightOperand) {
		return comparison(RtlVectorComparison.Operator.EQUAL, rightOperand);
	}

	default RtlBitSignal compareEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.EQUAL, rightOperand);
	}

	default RtlBitSignal compareNotEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default RtlBitSignal compareNotEqual(VectorValue rightOperand) {
		return comparison(RtlVectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default RtlBitSignal compareNotEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThan(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThan(VectorValue rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThan(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThanOrEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThanOrEqual(VectorValue rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThanOrEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThan(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThan(VectorValue rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThan(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThanOrEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThanOrEqual(VectorValue rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThanOrEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN_OR_EQUAL, rightOperand);
	}

	//
	// other
	//

	default RtlVectorRepetition repeat(int repetitions) {
		return new RtlVectorRepetition(getRtlItem().getRealm(), this, repetitions);
	}

	default RtlVectorSampler sampler(RtlClockNetwork clock) {
		return new RtlVectorSampler(clock, this);
	}

}
