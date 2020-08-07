package name.martingeisse.mahdl.gradle.codegen;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.processor.expression.*;
import name.martingeisse.mahdl.common.processor.statement.*;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.compiler.model.ContinuousDoBlockInfo;
import name.martingeisse.mahdl.compiler.model.GenerationModel;
import name.martingeisse.mahdl.input.cm.CmNode;
import name.martingeisse.mahdl.input.cm.impl.CmTokenImpl;
import name.martingeisse.mahdl.input.cm.impl.IElementType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 */
public class ContinuousStatementExpressionGenerator {

	private static final CmNode DUMMY_CM_NODE = new CmTokenImpl(0, 0, "", IElementType.WHITE_SPACE);

	private static final ProcessedExpression UNCHANGED = new UnknownExpression(DUMMY_CM_NODE);

	private final GenerationModel model;
	private final StringBuilder builder;

	public ContinuousStatementExpressionGenerator(GenerationModel model, StringBuilder builder) {
		this.model = model;
		this.builder = builder;
	}

	/**
	 * For a MaHDL continuous do-block and a signal that gets assigned to in that block, builds an equivalent
	 * Java expression to be used in an ESDK module constructor. The expression evaluates to an RtlBitSignal or
	 * RtlVector signal.
	 * <p>
	 * This method may use the string builder provided in the constructor to build helper expressions as Java
	 * constructor statements; therefore, this method must not be called while building a Java statement is in progress.
	 */
	public ProcessedExpression buildEquivalentExpression(ContinuousDoBlockInfo doBlockInfo,
														 ProcessedDataType type,
														 Predicate<ProcessedExpression> leftHandSideMatcher) {
		// We don't check whether the result is/contains UNCHANGED because the processing phase will already flag
		// that as an error.
		return buildEquivalentExpression(doBlockInfo.getDoBlock().getBody(), type, leftHandSideMatcher, UNCHANGED);
	}

	private ProcessedExpression buildEquivalentExpression(ProcessedStatement statement,
														  ProcessedDataType type,
														  Predicate<ProcessedExpression> leftHandSideMatcher,
														  ProcessedExpression soFar) {
		if (statement instanceof ProcessedBlock) {

			for (ProcessedStatement child : ((ProcessedBlock) statement).getStatements()) {
				soFar = buildEquivalentExpression(child, type, leftHandSideMatcher, soFar);
			}
			return soFar;

		} else if (statement instanceof ProcessedAssignment) {

			// TODO handle index-selection and range-selection targets.

			ProcessedAssignment assignment = (ProcessedAssignment) statement;
			ProcessedExpression leftHandSide = assignment.getLeftHandSide();
			if (leftHandSideMatcher.test(leftHandSide)) {
				return assignment.getRightHandSide();
			} else {
				return soFar;
			}

		} else if (statement instanceof ProcessedIf) {

			// Might do a real equals() check here, but that is currently not supported by ProcessedExpression. This is
			// not a real problem though since at worst we get a conditional expression with equal "then" and "else"
			// expressions.
			ProcessedIf processedIf = (ProcessedIf) statement;
			ProcessedExpression thenBranch = buildEquivalentExpression(processedIf.getThenBranch(), type, leftHandSideMatcher, soFar);
			ProcessedExpression elseBranch = buildEquivalentExpression(processedIf.getElseBranch(), type, leftHandSideMatcher, soFar);
			if (thenBranch == elseBranch) {
				// in particular, this handles the case that both sub-results are the same as soFar
				return thenBranch;
			}
			try {
				return new ProcessedConditional(statement.getErrorSource(), processedIf.getCondition(), thenBranch, elseBranch);
			} catch (TypeErrorException e) {
				return soFar; // error has been reported during processing already
			}

		} else if (statement instanceof ProcessedSwitchStatement) {

			ProcessedSwitchStatement processedSwitchStatement = (ProcessedSwitchStatement) statement;

			List<ProcessedSwitchExpression.Case> outputCases = new ArrayList<>();
			for (ProcessedSwitchStatement.Case inputCase : processedSwitchStatement.getCases()) {
				outputCases.add(new ProcessedSwitchExpression.Case(inputCase.getSelectorValues(),
					buildEquivalentExpression(inputCase.getBranch(), type, leftHandSideMatcher, soFar)));
			}

			ProcessedExpression outputDefaultBranch = processedSwitchStatement.getDefaultBranch() == null ? soFar :
				buildEquivalentExpression(processedSwitchStatement.getDefaultBranch(), type, leftHandSideMatcher, soFar);

			try {
				return new ProcessedSwitchExpression(statement.getErrorSource(), type,
					processedSwitchStatement.getSelector(), ImmutableList.copyOf(outputCases), outputDefaultBranch);
			} catch (TypeErrorException e) {
				return soFar; // error has been reported during processing already
			}

		} else {

			// NOP or unknown statement
			return soFar;

		}
	}

}
