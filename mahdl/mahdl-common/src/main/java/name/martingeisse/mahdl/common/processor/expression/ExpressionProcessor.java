/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.Expression;
import name.martingeisse.mahdl.input.cm.ExtendedExpression;

/**
 *
 */
public interface ExpressionProcessor {

	ExpressionProcessor ISOLATED = new ExpressionProcessorImpl(ProcessingSidekick.ISOLATED, LocalDefinitionResolver.NULL);

	ProcessedExpression process(ExtendedExpression expression);

	ProcessedExpression process(Expression expression);

	ProcessingSidekick getSidekick();

	// returns null on failure
	ConstantValue.Vector processCaseConstant(Expression caseConstantExpression, ProcessedDataType selectorDataType);

}
