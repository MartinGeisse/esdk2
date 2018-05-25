/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public interface RtlVectorSignal extends RtlSignal {

	int getWidth();

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

}
