package name.martingeisse.mahdl.gradle.codegen;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.*;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.gradle.CompilationErrors;
import name.martingeisse.mahdl.gradle.model.GenerationModel;

import java.math.BigInteger;

/**
 *
 */
public class AssignmentTargetGenerator {

	private final GenerationModel model;
	private final StringBuilder builder;
	private final ValueGenerator valueGenerator;
	private final ExpressionGenerator expressionGenerator;
	private final ProcessingSidekick sidekick;
	private final ProcessedExpression.FormallyConstantEvaluationContext evaluationContext;

	public AssignmentTargetGenerator(GenerationModel model, StringBuilder builder, ValueGenerator valueGenerator, ExpressionGenerator expressionGenerator, ProcessingSidekick sidekick) {
		this.model = model;
		this.builder = builder;
		this.valueGenerator = valueGenerator;
		this.expressionGenerator = expressionGenerator;
		this.sidekick = sidekick;
		this.evaluationContext = new ProcessedExpression.FormallyConstantEvaluationContext(sidekick);
	}

	/**
	 * For a MaHDL left-hand-side expression, builds a Java expression to be used in an ESDK module constructor.
	 * The expression evaluates to an RtlBitAssignmentTarget or RtlVectorAssignmentTarget corresponding to the MaHDL
	 * expression.
	 * <p>
	 * This method may use the string builder provided in the constructor to build helper expressions as Java
	 * constructor statements; therefore, this method must not be called while building a Java statement is in progress.
	 * <p>
	 * Assignment targets are only used in clocked do-blocks, not in continuous do-blocks. This implies that an instance
	 * port cannot be an assignment target since instance ports can only be assigned to continuously.
	 */
	public String buildAssignmentTarget(ProcessedExpression expression) {
		if (expression instanceof SignalLikeReference) {

			return "_" + ((SignalLikeReference) expression).getDefinition().getName();

		} else if (expression instanceof ProcessedBinaryOperation) {

			throw new UnsupportedOperationException("assignment to concat target not yet implemented");

		} else if (expression instanceof ProcessedIndexSelection) {

			ProcessedIndexSelection indexSelection = (ProcessedIndexSelection) expression;

			// handle container
			if (!(indexSelection.getContainer() instanceof SignalLikeReference)) {
				throw new UnsupportedOperationException("unsupported assignment target for code generation");
			}
			String container = "_" + ((SignalLikeReference) indexSelection.getContainer()).getDefinition().getName();

			// Handle selection by (constant) integer specially. It uses a different ESDK class that allows indexing
			// the "upper" indices of a non-PO2 container.
			if (indexSelection.getIndex().getDataType().getFamily() == ProcessedDataType.Family.INTEGER) {
				BigInteger index = indexSelection.getIndex().evaluateFormallyConstant(evaluationContext).convertToInteger();
				if (index == null) {
					// errors have been reported already
					return "null";
				} else {
					return container + ".selectTarget(" + index + ")";
				}
			}

			// select bit from vector or vector from procedural memory
			String index = expressionGenerator.buildExpression(indexSelection.getIndex());
			return container + ".selectTarget(" + index + ")";

		} else if (expression instanceof ProcessedRangeSelection) {

			ProcessedRangeSelection rangeSelection = (ProcessedRangeSelection) expression;
			if (!(rangeSelection.getContainer() instanceof SignalLikeReference)) {
				throw new UnsupportedOperationException("unsupported assignment target for code generation");
			}
			String container = "_" + ((SignalLikeReference) rangeSelection.getContainer()).getDefinition().getName();
			return "new RtlVectorTargetRangeSelection(realm, " + container + ", " + rangeSelection.getFromIndex() +
				", " + rangeSelection.getToIndex() + ")";

		}
		CompilationErrors.reportError(expression, "unsupported assignment target: " + expression);
		return "null";
	}

}
