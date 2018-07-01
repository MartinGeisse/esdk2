/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.signal;

/**
 *
 */
public interface RtlVectorSignal extends RtlSignal {

	int getWidth();

	RtlVectorValue getValue();

	//
	// selection
	//

	default RtlBitSignal select(RtlVectorSignal index) {
		return new RtlIndexSelection(getRtlItem().getDesign(), this, index);
	}

	default RtlBitSignal select(int index) {
		return new RtlConstantIndexSelection(getRtlItem().getDesign(), this, index);
	}

	default RtlVectorSignal select(int from, int to) {
		return new RtlRangeSelection(getRtlItem().getDesign(), this, from, to);
	}

	//
	// vector operations
	//

	default RtlVectorSignal operation(RtlVectorOperation.Operator operator, RtlVectorSignal rightOperand) {
		return new RtlVectorOperation(getRtlItem().getDesign(), operator, this, rightOperand);
	}

	default RtlVectorSignal operation(RtlVectorOperation.Operator operator, int rightOperand) {
		return operation(operator, RtlVectorConstant.from(getRtlItem().getDesign(), getWidth(), rightOperand));
	}

	default RtlVectorSignal add(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.ADD, rightOperand);
	}

	default RtlVectorSignal add(int rightOperand) {
		return operation(RtlVectorOperation.Operator.ADD, rightOperand);
	}

	default RtlVectorSignal subtract(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default RtlVectorSignal subtract(int rightOperand) {
		return operation(RtlVectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default RtlVectorSignal multiply(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default RtlVectorSignal multiply(int rightOperand) {
		return operation(RtlVectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default RtlVectorSignal and(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.AND, rightOperand);
	}

	default RtlVectorSignal and(int rightOperand) {
		return operation(RtlVectorOperation.Operator.AND, rightOperand);
	}

	default RtlVectorSignal or(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.OR, rightOperand);
	}

	default RtlVectorSignal or(int rightOperand) {
		return operation(RtlVectorOperation.Operator.OR, rightOperand);
	}

	default RtlVectorSignal xor(RtlVectorSignal rightOperand) {
		return operation(RtlVectorOperation.Operator.XOR, rightOperand);
	}

	default RtlVectorSignal xor(int rightOperand) {
		return operation(RtlVectorOperation.Operator.XOR, rightOperand);
	}

	//
	// comparisons
	//

	default RtlBitSignal comparison(RtlVectorComparison.Operator operator, RtlVectorSignal rightOperand) {
		return new RtlVectorComparison(getRtlItem().getDesign(), operator, this, rightOperand);
	}

	default RtlBitSignal comparison(RtlVectorComparison.Operator operator, int rightOperand) {
		return comparison(operator, RtlVectorConstant.from(getRtlItem().getDesign(), getWidth(), rightOperand));
	}

	default RtlBitSignal compareEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.EQUAL, rightOperand);
	}

	default RtlBitSignal compareEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.EQUAL, rightOperand);
	}

	default RtlBitSignal compareNotEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default RtlBitSignal compareNotEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThan(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThan(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThanOrEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedLessThanOrEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_LESS_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThan(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThan(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThanOrEqual(RtlVectorSignal rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN_OR_EQUAL, rightOperand);
	}

	default RtlBitSignal compareUnsignedGreaterThanOrEqual(int rightOperand) {
		return comparison(RtlVectorComparison.Operator.UNSIGNED_GREATER_THAN_OR_EQUAL, rightOperand);
	}

}
