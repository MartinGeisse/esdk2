/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor;

import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ProcessedConstantExpression;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.UnknownExpression;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;

import java.math.BigInteger;

/**
 *
 */
public class AssignmentConversionUtil {

	public static ProcessedExpression convertOnAssignment(ProcessedExpression expression, ProcessedDataType targetType, ProcessingSidekick sidekick) {
		if (expression.getDataType().equals(targetType)) {
			return expression;
		}
		if (expression.isUnknownType()) {
			sidekick.onDiagnostic(expression, "could not convert unknown expression on assignment");
			return expression;
		}
		if (expression.getDataType() instanceof ProcessedDataType.Integer && expression instanceof ProcessedConstantExpression) {
			BigInteger value = ((ProcessedConstantExpression) expression).getValue().convertToInteger();
			if (targetType instanceof ProcessedDataType.Bit) {
				if (value.equals(BigInteger.ZERO)) {
					return new ProcessedConstantExpression(expression.getErrorSource(), new ConstantValue.Bit(false));
				} else if (value.equals(BigInteger.ONE)) {
					return new ProcessedConstantExpression(expression.getErrorSource(), new ConstantValue.Bit(true));
				} else {
					sidekick.onError(expression.getErrorSource(), "cannot convert value " + value + " to bit");
					return new UnknownExpression(expression.getErrorSource());
				}
			} else if (targetType instanceof ProcessedDataType.Vector) {
				int targetSize = ((ProcessedDataType.Vector) targetType).getSize();
				try {
					return new ProcessedConstantExpression(expression.getErrorSource(), new ConstantValue.Vector(targetSize, value, false));
				} catch (ConstantValue.TruncateRequiredException e) {
					sidekick.onError(expression.getErrorSource(), e.getMessage());
					return new UnknownExpression(expression.getErrorSource());
				}
			}
		}
		sidekick.onError(expression.getErrorSource(), "cannot convert " + expression.getDataType() + " to type " + targetType);
		return new UnknownExpression(expression.getErrorSource());
	}

}
