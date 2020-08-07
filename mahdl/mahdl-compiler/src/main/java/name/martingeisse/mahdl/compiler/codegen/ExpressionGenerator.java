package name.martingeisse.mahdl.compiler.codegen;

import name.martingeisse.mahdl.common.functions.BuiltinFunction;
import name.martingeisse.mahdl.common.functions.RepeatFunction;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.*;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.compiler.CompilerContext;
import name.martingeisse.mahdl.compiler.model.GenerationModel;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

/**
 *
 */
public class ExpressionGenerator {

	private final GenerationModel model;
	private final StringBuilder builder;
	private final ValueGenerator valueGenerator;
	private final ProcessingSidekick sidekick;
	private final ProcessedExpression.FormallyConstantEvaluationContext evaluationContext;

	public ExpressionGenerator(GenerationModel model, StringBuilder builder, ValueGenerator valueGenerator, ProcessingSidekick sidekick) {
		this.model = model;
		this.builder = builder;
		this.valueGenerator = valueGenerator;
		this.sidekick = sidekick;
		this.evaluationContext = new ProcessedExpression.FormallyConstantEvaluationContext(sidekick);
	}

	/**
	 * For a MaHDL right-hand-side expression, builds a Java expression to be used in an ESDK module constructor.
	 * The expression evaluates to an RtlBitSignal or RtlVector signal corresponding to the MaHDL expression.
	 * <p>
	 * This method may use the string builder provided in the constructor to build helper expressions as Java
	 * constructor statements; therefore, this method must not be called while building a Java statement is in progress.
	 */
	public String buildExpression(ProcessedExpression expression) {

		// We rely on the MaHDL processor to have constant (sub-)expressions folded for us. So anything that cannot be
		// compiled to runtime constructs should have been disappeared by now.

		if (expression instanceof ProcessedConstantExpression) {

			String value = valueGenerator.buildValue(((ProcessedConstantExpression) expression).getValue());
			switch (expression.getDataType().getFamily()) {

				case BIT:
					return "new RtlBitConstant(realm, " + value + ")";

				case VECTOR:
					return "new RtlVectorConstant(realm, " + value + ")";

				default:
					CompilerContext.get().reportError(expression, "unsupported run-time value: " + expression.getDataType());
					return "null";

			}

		} else if (expression instanceof SignalLikeReference) {

			return "_" + ((SignalLikeReference) expression).getDefinition().getName();

		} else if (expression instanceof InstancePortReference) {

			InstancePortReference reference = (InstancePortReference) expression;
			return "_" + reference.getModuleInstance().getName() + ".get" + StringUtils.capitalize(reference.getPort().getName()) + "()";

		} else if (expression instanceof ProcessedUnaryOperation) {

			ProcessedUnaryOperation operation = (ProcessedUnaryOperation) expression;
			String operand = buildExpression(operation.getOperand());
			switch (operation.getOperator()) {

				case PLUS:
					return operand;

				case MINUS:
					return operand + ".negate()";

				case NOT:
					return operand + ".not()";

				default:
					CompilerContext.get().reportError(expression, "unsupported unary operator: " + operation.getOperator());
					return "null";

			}

		} else if (expression instanceof ProcessedBinaryOperation) {

			ProcessedBinaryOperation operation = (ProcessedBinaryOperation) expression;
			String left = buildExpression(operation.getLeftOperand());
			String right = buildExpression(operation.getRightOperand());
			switch (operation.getOperator()) {

				case AND:
					return left + ".and(" + right + ")";

				case OR:
					return left + ".or(" + right + ")";

				case XOR:
					return left + ".xor(" + right + ")";

				case PLUS:
					return left + ".add(" + right + ")";

				case MINUS:
					return left + ".subtract(" + right + ")";

				case TIMES:
					return left + ".multiply(" + right + ")";

				// these are only allowed in constant sub-expressions and so should have been folded
				case DIVIDED_BY:
				case REMAINDER:
					CompilerContext.get().reportError(expression, "run-time division / remainder are not supported");
					return "null";

				case VECTOR_CONCAT:
					return left + ".concat(" + right + ")";

				case SHIFT_LEFT:
					return "new RtlShiftOperation(realm, RtlShiftOperation.Direction.LEFT, " + left + ", " + right + ")";

				case SHIFT_RIGHT:
					return "new RtlShiftOperation(realm, RtlShiftOperation.Direction.RIGHT, " + left + ", " + right + ")";

				case EQUAL:
					return left + ".compareEqual(" + right + ")";

				case NOT_EQUAL:
					return left + ".compareNotEqual(" + right + ")";

				case LESS_THAN:
					return left + ".compareUnsignedLessThan(" + right + ")";

				case LESS_THAN_OR_EQUAL:
					return left + ".compareUnsignedLessThanOrEqual(" + right + ")";

				case GREATER_THAN:
					return left + ".compareUnsignedGreaterThan(" + right + ")";

				case GREATER_THAN_OR_EQUAL:
					return left + ".compareUnsignedGreaterThanOrEqual(" + right + ")";

				default:
					CompilerContext.get().reportError(expression, "unsupported binary operator: " + operation.getOperator());
					return "null";

			}

		} else if (expression instanceof ProcessedConditional) {

			ProcessedConditional conditional = (ProcessedConditional) expression;
			String condition = buildExpression(conditional.getCondition());
			String thenBranch = buildExpression(conditional.getThenBranch());
			String elseBranch = buildExpression(conditional.getElseBranch());
			return condition + ".conditional(" + thenBranch + ", " + elseBranch + ")";

		} else if (expression instanceof ProcessedIndexSelection) {

			ProcessedIndexSelection indexSelection = (ProcessedIndexSelection) expression;

			// handle LUTs specially since they don't use the .select() syntax
			if (indexSelection.getContainer().getDataType().getFamily() == ProcessedDataType.Family.MATRIX &&
				indexSelection.getContainer() instanceof ProcessedConstantExpression) {
				String matrix = valueGenerator.buildValue(((ProcessedConstantExpression) indexSelection.getContainer()).getValue());
				String index = buildExpression(indexSelection.getIndex());
				return "new RtlLookupTable(realm, " + matrix + ", " + index + ")";
			}

			// otherwise, the container is a normal run-time expression
			String container = buildExpression(indexSelection.getContainer());

			// Handle selection by (constant) integer specially. It uses a different ESDK class that allows indexing
			// the "upper" indices of a non-PO2 container.
			if (indexSelection.getIndex().getDataType().getFamily() == ProcessedDataType.Family.INTEGER) {
				BigInteger index = indexSelection.getIndex().evaluateFormallyConstant(evaluationContext).convertToInteger();
				if (index == null) {
					// errors have been reported already
					return "null";
				} else {
					return container + ".select(" + index + ")";
				}
			}

			// select bit from vector or vector from procedural memory
			String index = buildExpression(indexSelection.getIndex());
			return container + ".select(" + index + ")";

		} else if (expression instanceof ProcessedRangeSelection) {

			ProcessedRangeSelection rangeSelection = (ProcessedRangeSelection) expression;
			String container = buildExpression(rangeSelection.getContainer());
			return container + ".select(" + rangeSelection.getFromIndex() + ", " + rangeSelection.getToIndex() + ")";

		} else if (expression instanceof ProcessedSwitchExpression) {

			ProcessedSwitchExpression switchExpression = (ProcessedSwitchExpression) expression;
			String helperName = "___switchExpression" + model.newSyntheticConstruct();
			if (expression.getDataType().getFamily() == ProcessedDataType.Family.BIT) {
				builder.append("		RtlBitSwitchSignal ").append(helperName)
					.append(" = new RtlBitSwitchSignal(realm, ").append(buildExpression(switchExpression.getSelector()))
					.append(");\n");

			} else if (expression.getDataType().getFamily() == ProcessedDataType.Family.VECTOR) {
				int width = ((ProcessedDataType.Vector) expression.getDataType()).getSize();
				builder.append("		RtlVectorSwitchSignal ").append(helperName)
					.append(" = new RtlVectorSwitchSignal(realm, ").append(buildExpression(switchExpression.getSelector()))
					.append(", ").append(width).append(");\n");
			} else {
				CompilerContext.get().reportError(expression, "unsupported data type for switch expression");
				return "null";
			}
			for (ProcessedSwitchExpression.Case aCase : switchExpression.getCases()) {
				builder.append("		").append(helperName).append(".addCase(ImmutableList.of(")
					.append(valueGenerator.buildValues(aCase.getSelectorValues())).append("), ")
					.append(buildExpression(aCase.getResultValue())).append(");\n");
			}
			if (switchExpression.getDefaultBranch() != null) {
				builder.append("		").append(helperName).append(".setDefaultSignal(")
					.append(buildExpression(switchExpression.getDefaultBranch())).append(");\n");
			}
			return helperName;

		} else if (expression instanceof ProcessedFunctionCall) {

			ProcessedFunctionCall call = (ProcessedFunctionCall) expression;
			BuiltinFunction function = call.getFunction();
			if (function instanceof RepeatFunction) {
				return buildRepeatCall(call);
			} else {
				CompilerContext.get().reportError(expression, "unsupported run-time function call: " + ((ProcessedFunctionCall) expression).getFunction());
				return "null";
			}

		} else if (expression instanceof UnknownExpression) {

			// errors have been reported already
			return "null";

		} else {

			throw new IllegalArgumentException("unsupported expression type: " + expression);

		}
	}

	private String buildRepeatCall(ProcessedFunctionCall call) {
		if (call.getArguments().size() != 2) {
			return "null";
		}
		BigInteger repetitionCount = call.getArguments().get(0).evaluateFormallyConstant(evaluationContext).convertToInteger();
		String source = buildExpression(call.getArguments().get(1));
		return source + ".repeat(" + repetitionCount + ")";
	}

}
