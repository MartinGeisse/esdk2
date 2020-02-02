/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.mahdl.common.processor.AssignmentConversionUtil;
import name.martingeisse.mahdl.common.processor.AssignmentValidator;
import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.expression.ConstantValue;
import name.martingeisse.mahdl.common.processor.expression.ExpressionProcessor;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.TypeErrorException;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO this processor currently does not ensure that an asynchronous do-block assigns to each target signal in each
 * branch, nor does it check that assignments to clock signals only occur unconditionally.
 */
public final class StatementProcessor {

	private final ProcessingSidekick sidekick;
	private final ExpressionProcessor expressionProcessor;
	private final AssignmentValidator assignmentValidator;

	public StatementProcessor(ProcessingSidekick sidekick, ExpressionProcessor expressionProcessor, AssignmentValidator assignmentValidator) {
		this.sidekick = sidekick;
		this.expressionProcessor = expressionProcessor;
		this.assignmentValidator = assignmentValidator;
	}

	public ProcessedDoBlock process(ImplementationItem_DoBlock doBlock) {
		DoBlockTrigger trigger = doBlock.getTrigger();
		AssignmentValidator.TriggerKind triggerKind;
		ProcessedExpression clock;
		if (trigger instanceof DoBlockTrigger_Combinatorial) {
			triggerKind = AssignmentValidator.TriggerKind.CONTINUOUS;
			clock = null;
		} else if (trigger instanceof DoBlockTrigger_Clocked) {
			triggerKind = AssignmentValidator.TriggerKind.CLOCKED;
			Expression clockExpression = ((DoBlockTrigger_Clocked) trigger).getClockExpression();
			clock = sidekick.expectType(expressionProcessor.process(clockExpression), ProcessedDataType.Family.CLOCK);
		} else {
			error(trigger, "unknown trigger type");
			return null;
		}
		ProcessedStatement body = process(doBlock.getStatement(), triggerKind);
		ProcessedDoBlock result = new ProcessedDoBlock(clock, body);
		if (triggerKind == AssignmentValidator.TriggerKind.CONTINUOUS) {
			new AssignmentBranchCompletenessValidator(sidekick, result).run();
		}
		return result;
	}

	public ProcessedStatement process(Statement statement, AssignmentValidator.TriggerKind triggerKind) {
		if (statement instanceof Statement_Block) {

			Statement_Block block = (Statement_Block) statement;
			return processBlock(statement, block.getBody().getAll(), triggerKind);

		} else if (statement instanceof Statement_Assignment) {

			Statement_Assignment assignment = (Statement_Assignment) statement;
			ProcessedExpression leftHandSide = expressionProcessor.process(assignment.getLeftSide());
			ProcessedExpression rightHandSide = AssignmentConversionUtil.convertOnAssignment(
				expressionProcessor.process(assignment.getRightSide()),
				leftHandSide.getDataType(), sidekick);
			assignmentValidator.validateAssignmentTo(leftHandSide, triggerKind);
			if (leftHandSide.getDataType().getFamily() == ProcessedDataType.Family.MATRIX) {
				return error(statement, "cannot assign the whole matrix at once");
			}
			try {
				return new ProcessedAssignment(statement, leftHandSide, rightHandSide);
			} catch (TypeErrorException e) {
				return error(statement, "internal type error");
			}

		} else if (statement instanceof Statement_IfThen) {

			Statement_IfThen ifStatement = (Statement_IfThen) statement;
			return processIfStatement(statement, ifStatement.getCondition(), ifStatement.getThenBranch(), null, triggerKind);

		} else if (statement instanceof Statement_IfThenElse) {

			Statement_IfThenElse ifStatement = (Statement_IfThenElse) statement;
			return processIfStatement(statement, ifStatement.getCondition(), ifStatement.getThenBranch(), ifStatement.getElseBranch(), triggerKind);

		} else if (statement instanceof Statement_Switch) {

			return process((Statement_Switch) statement, triggerKind);

		} else {
			return error(statement, "unknown statement type");

		}
	}

	public ProcessedBlock processBlock(CmNode errorSource, List<Statement> statements, AssignmentValidator.TriggerKind triggerKind) {
		List<ProcessedStatement> processedBodyStatements = new ArrayList<>();
		for (Statement bodyStatement : statements) {
			processedBodyStatements.add(process(bodyStatement, triggerKind));
		}
		return new ProcessedBlock(errorSource, ImmutableList.copyOf(processedBodyStatements));
	}

	private ProcessedIf processIfStatement(CmNode errorSource, Expression condition, Statement thenBranch, Statement elseBranch, AssignmentValidator.TriggerKind triggerKind) {

		// condition
		ProcessedExpression processedCondition = sidekick.expectType(expressionProcessor.process(condition),
			ProcessedDataType.Family.BIT);

		// branches
		ProcessedStatement processedThenBranch = process(thenBranch, triggerKind);
		ProcessedStatement processedElseBranch;
		if (elseBranch == null) {
			processedElseBranch = new Nop(errorSource);
		} else {
			processedElseBranch = process(elseBranch, triggerKind);
		}

		return new ProcessedIf(errorSource, processedCondition, processedThenBranch, processedElseBranch);
	}

	private ProcessedStatement process(Statement_Switch switchStatement, AssignmentValidator.TriggerKind triggerKind) {
		ProcessedExpression selector = sidekick.expectType(expressionProcessor.process(switchStatement.getSelector()),
			ProcessedDataType.Family.VECTOR);
		boolean selectorOkay = !selector.isUnknownType();

		if (switchStatement.getItems().getAll().isEmpty()) {
			return error(switchStatement, "switch statement has no cases");
		}

		boolean errorInCases = false;
		Set<ConstantValue.Vector> foundSelectorValues = new HashSet<>();
		List<ProcessedSwitchStatement.Case> processedCases = new ArrayList<>();
		ProcessedStatement processedDefaultCase = null;
		for (StatementCaseItem caseItem : switchStatement.getItems().getAll()) {
			if (caseItem instanceof StatementCaseItem_Value) {
				StatementCaseItem_Value typedCaseItem = (StatementCaseItem_Value) caseItem;

				// result value expression gets handled below
				CmList<Statement> statementListNode = typedCaseItem.getStatements();
				ProcessedStatement caseStatement = processBlock(statementListNode, statementListNode.getAll(), triggerKind);

				// process selector values
				List<ConstantValue.Vector> caseSelectorValues = new ArrayList<>();
				for (Expression currentCaseSelectorExpression : typedCaseItem.getSelectorValues().getAll()) {
					ConstantValue.Vector selectorVectorValue = expressionProcessor.processCaseSelectorValue(currentCaseSelectorExpression, selector.getDataType());
					if (selectorVectorValue != null) {
						if (foundSelectorValues.add(selectorVectorValue)) {
							caseSelectorValues.add(selectorVectorValue);
						} else {
							error(currentCaseSelectorExpression, "duplicate selector value");
							errorInCases = true;
						}
					}
				}
				processedCases.add(new ProcessedSwitchStatement.Case(ImmutableList.copyOf(caseSelectorValues), caseStatement));

			} else if (caseItem instanceof StatementCaseItem_Default) {

				// result value expression gets handled below
				CmList<Statement> statementListNode = ((StatementCaseItem_Default) caseItem).getStatements();
				ProcessedStatement caseStatement = processBlock(statementListNode, statementListNode.getAll(), triggerKind);

				// remember default case and check for duplicate
				if (processedDefaultCase != null) {
					error(caseItem, "duplicate default case");
					errorInCases = true;
				} else {
					processedDefaultCase = caseStatement;
				}

			} else {
				error(caseItem, "unknown case item type");
				errorInCases = true;
			}
		}

		// in case of errors, don't return a switch expression
		if (!selectorOkay || errorInCases) {
			return new UnknownStatement(switchStatement);
		}

		// now build the switch expression
		try {
			return new ProcessedSwitchStatement(switchStatement, selector, ImmutableList.copyOf(processedCases), processedDefaultCase);
		} catch (TypeErrorException e) {
			return error(switchStatement, "internal error during type-check", e);
		}

	}

	/**
	 * This method is sometimes called with a sub-statement of the current statement as the error source, in case
	 * that error can be attributed to that sub-statement.
	 * <p>
	 * The same error source gets attached to the returned {@link UnknownStatement} as the error source for other
	 * error messages added later, which is wrong in principle. However, no further error messages should be generated
	 * at all, and so this wrong behavior should not matter.
	 */
	@NotNull
	private UnknownStatement error(@NotNull CmNode errorSource, @NotNull String message) {
		return error(errorSource, message, null);
	}

	/**
	 * Same as the above, but with an exception.
	 */
	@NotNull
	private UnknownStatement error(@NotNull CmNode errorSource, @NotNull String message, @Nullable Throwable exception) {
		sidekick.onError(errorSource, message, exception);
		return new UnknownStatement(errorSource);
	}

}
