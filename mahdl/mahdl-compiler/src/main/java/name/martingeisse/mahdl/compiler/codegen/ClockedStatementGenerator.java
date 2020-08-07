package name.martingeisse.mahdl.compiler.codegen;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.statement.*;
import name.martingeisse.mahdl.compiler.model.GenerationModel;

/**
 *
 */
public class ClockedStatementGenerator {

	private final GenerationModel model;
	private final StringBuilder builder;
	private final ValueGenerator valueGenerator;
	private final ExpressionGenerator expressionGenerator;
	private final AssignmentTargetGenerator assignmentTargetGenerator;

	public ClockedStatementGenerator(GenerationModel model,
									 StringBuilder builder,
									 ValueGenerator valueGenerator,
									 ExpressionGenerator expressionGenerator,
									 ProcessingSidekick sidekick) {
		this.model = model;
		this.builder = builder;
		this.valueGenerator = valueGenerator;
		this.expressionGenerator = expressionGenerator;
		this.assignmentTargetGenerator = new AssignmentTargetGenerator(model, builder, valueGenerator, expressionGenerator, sidekick);
	}

	/**
	 * For a MaHDL statement that is part of a clocked do-block, generates Java code (using the string builder that
	 * was passed to the constructor) that adds ESDK statements to the specified statement sequence.
	 */
	public void generateStatements(String sequence, ProcessedStatement statement) {
		if (statement instanceof ProcessedBlock) {

			for (ProcessedStatement child : ((ProcessedBlock) statement).getStatements()) {
				generateStatements(sequence, child);
			}

		} else if (statement instanceof ProcessedAssignment) {

			ProcessedAssignment assignment = (ProcessedAssignment) statement;
			String leftHandSide = assignmentTargetGenerator.buildAssignmentTarget(assignment.getLeftHandSide());
			String rightHandSide = expressionGenerator.buildExpression(assignment.getRightHandSide());
			builder.append("		").append(sequence).append(".assign(").append(leftHandSide).append(", ").append(rightHandSide).append(");\n");

		} else if (statement instanceof ProcessedIf) {

			ProcessedIf processedIf = (ProcessedIf) statement;
			String helperName = "___when" + model.newSyntheticConstruct();
			builder.append("		RtlWhenStatement ").append(helperName).append(" = ").append(sequence)
				.append(".when(").append(expressionGenerator.buildExpression(processedIf.getCondition())).append(");\n");
			generateStatements(helperName + ".getThenBranch()", processedIf.getThenBranch());
			generateStatements(helperName + ".getOtherwiseBranch()", processedIf.getElseBranch());

		} else if (statement instanceof ProcessedSwitchStatement) {

			ProcessedSwitchStatement processedSwitchStatement = (ProcessedSwitchStatement) statement;
			String helperName = "___switch" + model.newSyntheticConstruct();
			builder.append("		RtlSwitchStatement ").append(helperName).append(" = ").append(sequence).append(".switchOn(")
				.append(expressionGenerator.buildExpression(processedSwitchStatement.getSelector())).append(");\n");
			for (ProcessedSwitchStatement.Case aCase : processedSwitchStatement.getCases()) {
				String caseName = "___case" + model.newSyntheticConstruct();
				builder.append("		RtlStatementSequence ").append(caseName).append(" = ").append(helperName)
					.append(".addCase(ImmutableList.of(").append(valueGenerator.buildValues(aCase.getSelectorValues()))
					.append("));\n");
				generateStatements(caseName, aCase.getBranch());
			}
			if (processedSwitchStatement.getDefaultBranch() != null) {
				generateStatements(helperName + ".getDefaultBranch()", processedSwitchStatement.getDefaultBranch());
			}

		}
	}

}
