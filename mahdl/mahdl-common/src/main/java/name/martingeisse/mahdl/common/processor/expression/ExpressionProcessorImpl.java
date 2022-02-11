/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.expression;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.functions.BuiltinFunction;
import name.martingeisse.mahdl.common.functions.BuiltinFunctions;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.definition.*;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.common.util.LiteralParser;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ExpressionProcessorImpl implements ExpressionProcessor {

	private final ProcessingSidekick sidekick;
	private final LocalDefinitionResolver localDefinitionResolver;

	public ExpressionProcessorImpl(ProcessingSidekick sidekick, LocalDefinitionResolver localDefinitionResolver) {
		this.sidekick = sidekick;
		this.localDefinitionResolver = localDefinitionResolver;
	}

	public ProcessedExpression process(ExtendedExpression expression) {
		try {
			if (expression instanceof ExtendedExpression_Normal) {
				return process(((ExtendedExpression_Normal) expression).getExpression());
			} else if (expression instanceof ExtendedExpression_Switch) {
				return process((ExtendedExpression_Switch) expression).performFolding(sidekick);
			} else {
				return error(expression, "unknown expression type");
			}
		} catch (TypeErrorException e) {
			return error(expression, "internal error during type-check of extended expression", e);
		}
	}

	private ProcessedExpression process(ExtendedExpression_Switch expression) throws TypeErrorException {

		ProcessedExpression selector = process(expression.getSelector());
		boolean selectorOkay = (selector.getDataType() instanceof ProcessedDataType.Vector);
		if (!selectorOkay) {
			error(expression.getSelector(), "selector must be of vector type, found " + selector.getDataType());
		}

		if (expression.getItems().getAll().isEmpty()) {
			return error(expression, "switch expression has no cases");
		}

		ProcessedDataType resultValueType = null;
		boolean errorInCases = false;
		Set<ConstantValue.Vector> foundSelectorValues = new HashSet<>();
		List<ProcessedSwitchExpression.Case> processedCases = new ArrayList<>();
		ProcessedExpression processedDefaultCase = null;
		for (ExpressionCaseItem caseItem : expression.getItems().getAll()) {
			ProcessedExpression resultValueExpression;
			if (caseItem instanceof ExpressionCaseItem_Value) {
				ExpressionCaseItem_Value typedCaseItem = (ExpressionCaseItem_Value) caseItem;

				// result value expression gets handled below
				resultValueExpression = process(typedCaseItem.getResultValue());

				// process selector values
				List<ConstantValue.Vector> caseSelectorValues = new ArrayList<>();
				for (Expression currentCaseConstantExpression : typedCaseItem.getSelectorValues().getAll()) {
					ConstantValue.Vector selectorVectorValue = processCaseConstant(currentCaseConstantExpression, selector.getDataType());
					if (selectorVectorValue != null) {
						if (foundSelectorValues.add(selectorVectorValue)) {
							caseSelectorValues.add(selectorVectorValue);
						} else {
							error(currentCaseConstantExpression, "duplicate selector value");
							errorInCases = true;
						}
					}
				}
				processedCases.add(new ProcessedSwitchExpression.Case(ImmutableList.copyOf(caseSelectorValues), resultValueExpression));

			} else if (caseItem instanceof ExpressionCaseItem_Default) {

				// result value expression gets handled below
				resultValueExpression = process(((ExpressionCaseItem_Default) caseItem).getResultValue());

				// remember default case and check for duplicate
				if (processedDefaultCase != null) {
					error(caseItem, "duplicate default case");
					errorInCases = true;
				} else {
					processedDefaultCase = resultValueExpression;
				}

			} else {
				error(caseItem, "unknown case item type");
				errorInCases = true;
				continue;
			}

			// now handle the result value expression
			if (resultValueExpression.getDataType() instanceof ProcessedDataType.Clock) {
				error(caseItem, "result of a select expression cannot have clock type");
			} else if (resultValueType == null) {
				resultValueType = resultValueExpression.getDataType();
			}

		}

		// try to convert all result value expressions to the common result value type
		if (resultValueType == null) {
			return error(expression, "internal error: could not determine result type of switch expression");
		}
		for (ProcessedSwitchExpression.Case aCase : processedCases) {
			if (!aCase.getResultValue().getDataType().equals(resultValueType)) {
				if (!aCase.getResultValue().isUnknownType() && !resultValueType.isUnknown()) {
					sidekick.onError(aCase.getResultValue().getErrorSource(), "conflicting result types: " +
						aCase.getResultValue().getDataType() + " vs. " + resultValueType);
				}
				errorInCases = true;
			}
		}
		if (processedDefaultCase != null) {
			if (!processedDefaultCase.getDataType().equals(resultValueType)) {
				if (!processedDefaultCase.isUnknownType() && !resultValueType.isUnknown()) {
					sidekick.onError(processedDefaultCase.getErrorSource(), "conflicting result types: " +
						processedDefaultCase.getDataType() + " vs. " + resultValueType);
				}
				errorInCases = true;
			}
		}

		// check for missing selector values
		if (processedDefaultCase == null && selector.getDataType() instanceof ProcessedDataType.Vector) {
			int selectorSize = ((ProcessedDataType.Vector) selector.getDataType()).getSize();
			if (foundSelectorValues.size() != (1 << selectorSize)) {
				return error(expression, "incomplete switch expression");
			}
		}

		// in case of errors, don't return a switch expression
		if (!selectorOkay || errorInCases) {
			return new UnknownExpression(expression);
		}

		// now build the switch expression
		return new ProcessedSwitchExpression(expression, resultValueType, selector, ImmutableList.copyOf(processedCases), processedDefaultCase);

	}

	public ProcessedExpression process(Expression expression) {
		return processWithoutFolding(expression).performFolding(sidekick);
	}

	private ProcessedExpression processWithoutFolding(Expression expression) {
		try {
			if (expression instanceof Expression_Literal) {
				return process((Expression_Literal) expression);
			} else if (expression instanceof Expression_Identifier) {
				return process((Expression_Identifier) expression);
			} else if (expression instanceof Expression_InstancePort) {
				return process((Expression_InstancePort) expression);
			} else if (expression instanceof Expression_IndexSelection) {
				return process((Expression_IndexSelection) expression);
			} else if (expression instanceof Expression_RangeSelection) {
				return process((Expression_RangeSelection) expression);
			} else if (expression instanceof UnaryOperation) {
				return process((UnaryOperation) expression);
			} else if (expression instanceof BinaryOperation) {
				return process((BinaryOperation) expression);
			} else if (expression instanceof Expression_Conditional) {
				return process((Expression_Conditional) expression);
			} else if (expression instanceof Expression_FunctionCall) {
				return process((Expression_FunctionCall) expression);
			} else if (expression instanceof Expression_Parenthesized) {
				return process(((Expression_Parenthesized) expression).getExpression());
			} else {
				return error(expression, "unknown expression type");
			}
		} catch (TypeErrorException e) {
			return error(expression, "internal error during type-check of expression", e);
		}
	}

	private ProcessedExpression process(Expression_Literal expression) {
		try {
			return new ProcessedConstantExpression(expression, LiteralParser.parseLiteral(expression));
		} catch (LiteralParser.ParseException e) {
			return error(expression, e.getMessage());
		}
	}

	private ProcessedExpression process(Expression_Identifier expression) {
		String name = expression.getIdentifier().getText();
		Named definition = localDefinitionResolver.getDefinition(name);
		if (definition == null) {
			return error(expression, "cannot resolve symbol '" + name + "'");
		} else if (definition instanceof SignalLike) {
			return new SignalLikeReference(expression, (SignalLike) definition);
		} else if (definition instanceof ModuleInstance || definition instanceof ModuleInstanceWithMissingDefinition) {
			return error(expression, "cannot use a module instance directly in an expression");
		} else {
			return error(expression, "symbol '" + name + "' does not refer to a signal-like");
		}
	}

	private ProcessedExpression process(Expression_InstancePort expression) {

		// find the module instance
		ModuleInstance moduleInstance;
		{
			String instanceName = expression.getInstanceName().getIdentifier().getText();
			Named moduleInstanceCandidate = localDefinitionResolver.getDefinition(instanceName);
			if (moduleInstanceCandidate == null) {
				return error(expression.getInstanceName(), "cannot resolve symbol '" + instanceName + "'");
			} else if (moduleInstanceCandidate instanceof ModuleInstance) {
				moduleInstance = (ModuleInstance) moduleInstanceCandidate;
			} else if (moduleInstanceCandidate instanceof ModuleInstanceWithMissingDefinition) {
				QualifiedModuleName moduleNameElement = ((ModuleInstanceWithMissingDefinition) moduleInstanceCandidate).getModuleNameElement();
				String moduleName = CmUtil.canonicalizeQualifiedModuleName(moduleNameElement);
				return error(expression.getInstanceName(), "missing module '" + moduleName + "' for instance '" + instanceName + "'");
			} else {
				return error(expression.getInstanceName(), instanceName + " is not a module instance");
			}
		}

		// resolve the port reference
		String portName = expression.getPortName().getIdentifier().getText();
		InstancePort port = moduleInstance.getPorts().get(portName);
		if (port == null) {
			return error(expression, "cannot resolve port '" + portName + "' of instance '" + moduleInstance.getName() +
				"' of module " + moduleInstance.getModuleApi().getModuleIdentifier());
		}
		return new InstancePortReference(expression, moduleInstance, port);

	}

	private ProcessedExpression process(Expression_IndexSelection expression) throws TypeErrorException {

		ProcessedExpression container = process(expression.getContainer());
		int containerSizeIfKnown = determineContainerSize(container, true, "index-select");

		ProcessedExpression index = process(expression.getIndex());
		index = handleIndex(index, containerSizeIfKnown);

		if (containerSizeIfKnown == -1 || index instanceof UnknownExpression) {
			return new UnknownExpression(expression);
		} else {
			if (container.getDataType() instanceof ProcessedDataType.Vector) {
				return new ProcessedIndexSelection.BitFromVector(expression, container, index);
			} else if (container.getDataType() instanceof ProcessedDataType.Matrix) {
				return new ProcessedIndexSelection.VectorFromMatrix(expression, container, index);
			} else {
				return error(expression, "unknown container type");
			}
		}

	}

	private ProcessedExpression process(Expression_RangeSelection expression) throws TypeErrorException {

		// evaluate container
		ProcessedExpression container = process(expression.getContainer());
		int containerSizeIfKnown = determineContainerSize(container, false, "range-select");

		// evaluate from-index
		ProcessedExpression fromIndex = process(expression.getFrom());
		Integer fromIndexInteger = evaluateLocalSmallIntegerExpressionThatMustBeFormallyConstant(fromIndex);

		// evaluate to-index
		ProcessedExpression toIndex = process(expression.getTo());
		Integer toIndexInteger = evaluateLocalSmallIntegerExpressionThatMustBeFormallyConstant(toIndex);

		// stop here if any of them failed
		if (containerSizeIfKnown < 0 || fromIndexInteger == null || toIndexInteger == null) {
			return new UnknownExpression(expression);
		}

		// check if the range is invalid
		if (toIndexInteger < 0 || fromIndexInteger < toIndexInteger || fromIndexInteger >= containerSizeIfKnown) {
			return error(expression, "invalid range: " + fromIndexInteger + ":" + toIndexInteger +
				" for container size " + containerSizeIfKnown);
		}

		// otherwise return a range selection node
		int width = fromIndexInteger - toIndexInteger + 1;
		return new ProcessedRangeSelection(expression, new ProcessedDataType.Vector(width), container, fromIndexInteger, toIndexInteger);

	}

	private int determineContainerSize(@NotNull ProcessedExpression container, boolean allowMatrix, String operatorVerb) {
		ProcessedDataType type = container.getDataType();
		if (type instanceof ProcessedDataType.Unknown) {
			return -1;
		} else if (type instanceof ProcessedDataType.Vector) {
			return ((ProcessedDataType.Vector) type).getSize();
		} else if (allowMatrix && type instanceof ProcessedDataType.Matrix) {
			return ((ProcessedDataType.Matrix) type).getFirstSize();
		} else {
			error(container, "cannot " + operatorVerb + " from an expression of type " + type.getFamily().getDisplayString());
			return -1;
		}
	}

	private ProcessedExpression handleIndex(@NotNull ProcessedExpression index, int containerSizeIfKnown) {
		if (index.getDataType() instanceof ProcessedDataType.Integer) {
			Integer indexValue = evaluateLocalSmallIntegerExpressionThatMustBeFormallyConstant(index);
			if (indexValue == null || containerSizeIfKnown < 0) {
				return new UnknownExpression(index.getErrorSource());
			} else if (indexValue < 0 || indexValue >= containerSizeIfKnown) {
				return error(index, "invalid index for container size " + containerSizeIfKnown + ": " + indexValue);
			} else {
				return index;
			}
		} else if (index.getDataType() instanceof ProcessedDataType.Vector) {
			if (containerSizeIfKnown < 0) {
				return new UnknownExpression(index.getErrorSource());
			} else {
				// For a vector, the greatest possible value is releant, not the actual value, even if the vector is
				// constant (see language design documents for details).
				int indexSize = ((ProcessedDataType.Vector) index.getDataType()).getSize();
				if (indexSize >= ProcessedDataType.LOG2_SIZE_LIMIT) {
					return error(index, "index of vector size " + indexSize + " is too large for any supported container size");
				} else if (containerSizeIfKnown < (1 << indexSize)) {
					return error(index, "index of vector size " + indexSize +
						" must index a container of at least " + (1 << indexSize) + " in size, found " +
						containerSizeIfKnown);
				} else {
					return index;
				}
			}
		} else {
			return error(index, "cannot use an expression of type " + index.getDataType().getFamily() + " as index");
		}
	}

	private ProcessedExpression process(UnaryOperation expression) {
		ProcessedExpression operand = process(expression.getOperand());
		if (operand.getDataType() instanceof ProcessedDataType.Unknown) {
			return new UnknownExpression(expression);
		}
		ProcessedUnaryOperator operator = ProcessedUnaryOperator.from(expression);
		// unary operators have simple type handling -- we can even use the safety check and TypeErrorException to
		// detect errors without checking ourselves.
		try {
			return new ProcessedUnaryOperation(expression, operand, operator);
		} catch (TypeErrorException e) {
			return error(expression, "cannot apply operator " + operator + " to an operand of type " + operand.getDataType());
		}
	}

	private ProcessedExpression process(BinaryOperation expression) throws TypeErrorException {
		ProcessedExpression leftOperand = process(expression.getLeftOperand());
		ProcessedExpression rightOperand = process(expression.getRightOperand());
		if (leftOperand.getDataType() instanceof ProcessedDataType.Unknown || rightOperand.getDataType() instanceof ProcessedDataType.Unknown) {
			return new UnknownExpression(expression);
		}

		// handle concatenation operator -- it can have one of two entirely different meanings and has complex type handling
		if (expression instanceof Expression_BinaryConcat) {
			return handleConcatenation((Expression_BinaryConcat) expression, leftOperand, rightOperand);
		}
		ProcessedBinaryOperator operator = ProcessedBinaryOperator.from(expression);

		// Now, only logical operators can handle bit values, and only if both operands are bits.
		if ((leftOperand.getDataType() instanceof ProcessedDataType.Bit) != (rightOperand.getDataType() instanceof ProcessedDataType.Bit)) {
			return error(expression, "this operator cannot be used for " + leftOperand.getDataType().getFamily() +
				" and " + rightOperand.getDataType().getFamily() + " operands");
		}
		if (leftOperand.getDataType() instanceof ProcessedDataType.Bit) {
			return new ProcessedBinaryOperation(expression, leftOperand, rightOperand, operator);
		}

		// all other binary operators are IVOs
		{
			boolean error = false;
			if (!(leftOperand.getDataType() instanceof ProcessedDataType.Vector) && !(leftOperand.getDataType() instanceof ProcessedDataType.Integer)) {
				error = true;
			}
			if (!(rightOperand.getDataType() instanceof ProcessedDataType.Vector) && !(rightOperand.getDataType() instanceof ProcessedDataType.Integer)) {
				error = true;
			}
			if (error) {
				return error(expression, "cannot apply operator " + operator + " to operands of type " + leftOperand.getDataType() + " and " + rightOperand.getDataType());
			}
		}

		// handle TAIVOs (shift operators) specially (no conversion; result type is that of the left operand)
		if (expression instanceof Expression_BinaryShiftLeft || expression instanceof Expression_BinaryShiftRight) {
			if (leftOperand.getDataType() instanceof ProcessedDataType.Integer && rightOperand.getDataType() instanceof ProcessedDataType.Vector) {
				return error(expression, "a shift operator with an integer left operand must have an integer right operand too");
			}
			return new ProcessedBinaryOperation(expression, leftOperand, rightOperand, operator);
		}

		// handle TSIVOs
		if (!rightOperand.getDataType().equals(leftOperand.getDataType())) {
			return error(expression, "cannot apply operator " + operator + " to operands of type " + leftOperand.getDataType() + " and " + rightOperand.getDataType());
		}
		return new ProcessedBinaryOperation(expression, leftOperand, rightOperand, operator);

	}

	private ProcessedExpression handleConcatenation(Expression_BinaryConcat expression,
													ProcessedExpression leftOperand,
													ProcessedExpression rightOperand) throws TypeErrorException {

		// handle text concatenation
		if (leftOperand.getDataType() instanceof ProcessedDataType.Text || rightOperand.getDataType() instanceof ProcessedDataType.Text) {
			return new ProcessedBinaryOperation(expression, leftOperand, rightOperand, ProcessedBinaryOperator.TEXT_CONCAT);
		}

		// handle bit / vector concatenation
		boolean typeError = false;
		if (!(leftOperand.getDataType() instanceof ProcessedDataType.Bit) && !(leftOperand.getDataType() instanceof ProcessedDataType.Vector)) {
			typeError = true;
		}
		if (!(rightOperand.getDataType() instanceof ProcessedDataType.Bit) && !(rightOperand.getDataType() instanceof ProcessedDataType.Vector)) {
			typeError = true;
		}
		if (typeError) {
			return error(expression, "cannot apply concatenation operator to operands of type " + leftOperand.getDataType() + " and " + rightOperand.getDataType());
		} else {
			return new ProcessedBinaryOperation(expression, leftOperand, rightOperand, ProcessedBinaryOperator.VECTOR_CONCAT);
		}

	}

	private ProcessedExpression process(Expression_Conditional expression) throws TypeErrorException {

		// process sub-expressions
		ProcessedExpression condition = sidekick.expectType(process(expression.getCondition()), ProcessedDataType.Family.BIT);
		ProcessedExpression thenBranch = process(expression.getThenBranch());
		ProcessedExpression elseBranch = process(expression.getElseBranch());
		boolean error = condition.isUnknownType();

		// handle branch types
		if (thenBranch.getDataType() instanceof ProcessedDataType.Clock) {
			error(thenBranch, "cannot use a signal of clock type in a conditional expression");
			error = true;
		}
		if (elseBranch.getDataType() instanceof ProcessedDataType.Clock) {
			error(elseBranch, "cannot use a signal of clock type in a conditional expression");
			error = true;
		}
		if (!thenBranch.getDataType().equals(elseBranch.getDataType())) {
			if (!thenBranch.isUnknownType() && !elseBranch.isUnknownType()) {
				error(elseBranch, "type mismatch in then/else branches: " + thenBranch.getDataType() + " vs. " + elseBranch.getDataType());
			}
			error = true;
		}

		// check for errors
		if (error) {
			return new UnknownExpression(expression);
		} else {
			return new ProcessedConditional(expression, condition, thenBranch, elseBranch);
		}

	}

	private ProcessedExpression process(Expression_FunctionCall expression) {
		boolean error = false;

		FunctionName functionNameNode = expression.getFunctionName();
		String functionName;
		if (functionNameNode instanceof FunctionName_Identifier) {
			functionName = ((FunctionName_Identifier) functionNameNode).getValue().getText();
		} else if (functionNameNode instanceof FunctionName_Bit) {
			functionName = "bit";
		} else {
			return error(expression.getFunctionName(), "unknown function name node type: " + functionNameNode);
		}

		BuiltinFunction builtinFunction = BuiltinFunctions.FUNCTIONS.get(functionName);
		if (builtinFunction == null) {
			error(expression.getFunctionName(), "unknown function: " + functionName);
			error = true;
		}

		List<ProcessedExpression> arguments = new ArrayList<>();
		for (Expression argumentExpression : expression.getArguments().getAll()) {
			ProcessedExpression argument = process(argumentExpression);
			if (argument.getDataType() instanceof ProcessedDataType.Unknown) {
				error = true;
			}
			arguments.add(argument);
		}

		if (error) {
			return new UnknownExpression(expression);
		}

		ProcessedDataType returnType = builtinFunction.checkType(expression, arguments, sidekick);
		if (returnType instanceof ProcessedDataType.Unknown) {
			return new UnknownExpression(expression);
		}

		return new ProcessedFunctionCall(expression, returnType, builtinFunction, ImmutableList.copyOf(arguments));
	}

	private ConstantValue evaluateLocalExpressionThatMustBeFormallyConstant(ProcessedExpression expression) {
		return expression.evaluateFormallyConstant(
			new ProcessedExpression.FormallyConstantEvaluationContext(sidekick));
	}

	private Integer evaluateLocalSmallIntegerExpressionThatMustBeFormallyConstant(ProcessedExpression expression) {
		if (expression.getDataType().getFamily() != ProcessedDataType.Family.INTEGER) {
			sidekick.onError(expression.getErrorSource(), "expected integer type, found " + expression.getDataType());
			return null;
		}
		ConstantValue value = evaluateLocalExpressionThatMustBeFormallyConstant(expression);
		if (value.isUnknown()) {
			return null;
		}
		BigInteger integerValue = value.convertToInteger();
		if (integerValue == null) {
			sidekick.onError(expression.getErrorSource(), "could not get integer value of " + value);
			return null;
		}
		try {
			return integerValue.intValueExact();
		} catch (ArithmeticException e) {
			sidekick.onError(expression.getErrorSource(), "value too large: " + integerValue);
			return null;
		}
	}

	/**
	 * This method is sometimes called with a sub-expression of the current expression as the error source, in case
	 * that error can be attributed to that sub-expression. For example, if an operand of an operator has a type
	 * the operator can't handle, then the error message will be attached to the operand, not the whole binary
	 * expression.
	 * <p>
	 * The same error source gets attached to the returned {@link UnknownExpression} as the error source for other
	 * error messages added later. This is wrong in principle, since those error messages should be attached to the
	 * whole parent expression instead. However, since the return value right now is an UnknownExpression, no further
	 * error messages should be generated at all, and so this wrong behavior should not matter.
	 */
	@NotNull
	private ProcessedExpression error(@NotNull CmNode errorSource, @NotNull String message) {
		return error(errorSource, message, null);
	}

	/**
	 * Like the above, but with an exception that may be output (e.g. to the Gradle log).
	 */
	@NotNull
	private ProcessedExpression error(@NotNull CmNode errorSource, @NotNull String message, @Nullable Throwable exception) {
		sidekick.onError(errorSource, message, exception);
		return new UnknownExpression(errorSource);
	}

	/**
	 * the same note as for the other error method above applies to this one
	 */
	@NotNull
	private ProcessedExpression error(@NotNull ProcessedExpression processedExpression, @NotNull String message) {
		return error(processedExpression.getErrorSource(), message);
	}

	/**
	 * the same note as for the other error method above applies to this one
	 */
	@NotNull
	private ProcessedExpression error(@NotNull ProcessedExpression processedExpression, @NotNull String message, @Nullable Throwable exception) {
		return error(processedExpression.getErrorSource(), message, exception);
	}

	public ProcessingSidekick getSidekick() {
		return sidekick;
	}

	@Override
	public ConstantValue.Vector processCaseConstant(Expression caseConstantExpression, ProcessedDataType selectorDataType) {
		ProcessedExpression processedCaseConstantExpression = process(caseConstantExpression);
		ConstantValue caseConstantValue = evaluateLocalExpressionThatMustBeFormallyConstant(processedCaseConstantExpression);
		if (caseConstantValue instanceof ConstantValue.Unknown) {
			return null;
		}
		if (!(caseConstantValue instanceof ConstantValue.Vector)) {
			error(caseConstantExpression, "case constant must be a vector, found " + caseConstantValue.getDataType());
			return null;
		}
		if (selectorDataType instanceof ProcessedDataType.Vector) {
			int caseConstantVectorSize = ((ConstantValue.Vector) caseConstantValue).getSize();
			int selectorVectorSize = ((ProcessedDataType.Vector) selectorDataType).getSize();
			if (caseConstantVectorSize != selectorVectorSize) {
				error(caseConstantExpression, "case constant vector size (" + caseConstantVectorSize +
						") does not match selector vector size (" + selectorVectorSize + ")");
				return null;
			}
		}
		return (ConstantValue.Vector) caseConstantValue;
	}

}
