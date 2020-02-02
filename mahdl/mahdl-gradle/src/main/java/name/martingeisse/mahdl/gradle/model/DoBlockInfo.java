package name.martingeisse.mahdl.gradle.model;

import name.martingeisse.mahdl.common.processor.definition.SignalLike;
import name.martingeisse.mahdl.common.processor.expression.*;
import name.martingeisse.mahdl.common.processor.statement.BranchVisitor;
import name.martingeisse.mahdl.common.processor.statement.ProcessedDoBlock;
import name.martingeisse.mahdl.common.processor.statement.ProcessedStatement;

/**
 *
 */
public abstract class DoBlockInfo {

	private final String name;
	private final ProcessedDoBlock doBlock;

	public DoBlockInfo(String name, ProcessedDoBlock doBlock) {
		this.name = name;
		this.doBlock = doBlock;
	}

	public String getName() {
		return name;
	}

	public ProcessedDoBlock getDoBlock() {
		return doBlock;
	}

	protected void analyze(ProcessedStatement statement) {
		BranchVisitor.createAssignmentsWithoutResultVisitor(assignment -> analyzeAssignmentTo(assignment.getLeftHandSide())).visit(statement);
	}

	protected void analyzeAssignmentTo(ProcessedExpression destination) {
		if (destination instanceof SignalLikeReference) {
			analyzeAssignmentTo(((SignalLikeReference) destination).getDefinition());
		} else if (destination instanceof InstancePortReference) {
			analyzeAssignmentTo((InstancePortReference) destination);
		} else if (destination instanceof ProcessedIndexSelection) {
			analyzeAssignmentTo(((ProcessedIndexSelection) destination).getContainer());
		} else if (destination instanceof ProcessedRangeSelection) {
			analyzeAssignmentTo(((ProcessedRangeSelection) destination).getContainer());
		} else if (destination instanceof ProcessedBinaryOperation) {
			ProcessedBinaryOperation binaryOperation = (ProcessedBinaryOperation) destination;
			if (binaryOperation.getOperator() == ProcessedBinaryOperator.VECTOR_CONCAT) {
				analyzeAssignmentTo(binaryOperation.getLeftOperand());
				analyzeAssignmentTo(binaryOperation.getRightOperand());
			}
		}
	}

	protected abstract void analyzeAssignmentTo(SignalLike destination);

	protected abstract void analyzeAssignmentTo(InstancePortReference destination);

}
