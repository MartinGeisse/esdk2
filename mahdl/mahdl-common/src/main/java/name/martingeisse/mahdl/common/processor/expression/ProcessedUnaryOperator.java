/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.Expression_UnaryMinus;
import name.martingeisse.mahdl.input.cm.Expression_UnaryNot;
import name.martingeisse.mahdl.input.cm.Expression_UnaryPlus;
import name.martingeisse.mahdl.input.cm.UnaryOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public enum ProcessedUnaryOperator {

	NOT(ProcessedDataType.Family.BIT, ProcessedDataType.Family.VECTOR, ProcessedDataType.Family.INTEGER),
	PLUS(ProcessedDataType.Family.VECTOR, ProcessedDataType.Family.INTEGER),
	MINUS(ProcessedDataType.Family.VECTOR, ProcessedDataType.Family.INTEGER);

	private final ProcessedDataType.Family[] acceptedOperandFamilies;

	ProcessedUnaryOperator(@NotNull ProcessedDataType.Family... acceptedOperandFamilies) {
		this.acceptedOperandFamilies = acceptedOperandFamilies;
	}

	@NotNull
	public static ProcessedUnaryOperator from(@NotNull UnaryOperation operation) {
		if (operation instanceof Expression_UnaryNot) {
			return NOT;
		} else if (operation instanceof Expression_UnaryPlus) {
			return PLUS;
		} else if (operation instanceof Expression_UnaryMinus) {
			return MINUS;
		} else {
			throw new IllegalArgumentException("unknown unary operation: " + operation);
		}
	}

	@NotNull
	public ProcessedDataType checkType(@NotNull ProcessedDataType operandType) throws TypeErrorException {
		// TODO applying a unary NOT to a clock signal is allowed when it happens as part of a do-block trigger
		// (indicating a negative edge triggered register). This is currently not implemented.
		if (operandType instanceof ProcessedDataType.Unknown) {
			return operandType;
		}
		if (!ArrayUtils.contains(acceptedOperandFamilies, operandType.getFamily())) {
			throw new TypeErrorException("invalid operand type " + operandType + " for unary operator " + this);
		}
		return operandType;
	}

}
