package name.martingeisse.mahdl.common.processor.statement;

import name.martingeisse.mahdl.common.processor.ProcessingSidekick;
import name.martingeisse.mahdl.common.processor.definition.SignalLike;
import name.martingeisse.mahdl.common.processor.expression.*;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Validates that a do-block performs all assignments in a branch-complete way, that is, all targets are assigned
 * to no matter which branches are taken. This is only done for continuous do-blocks, not clocked ones (since clocked
 * registers are able to keep their previous state), but this validator ignores the trigger.
 */
class AssignmentBranchCompletenessValidator {

	private final ProcessingSidekick sidekick;
	private final ProcessedDoBlock doBlock;
	private final Set<SignalLike> signalLikes = new HashSet<>();
	private final Set<InstancePortReference> instancePortReferences = new HashSet<>();

	AssignmentBranchCompletenessValidator(ProcessingSidekick sidekick, ProcessedDoBlock doBlock) {
		this.sidekick = sidekick;
		this.doBlock = doBlock;
	}

	void run() {
		BranchVisitor.createAssignmentsWithoutResultVisitor(assignment -> collectTargets(assignment.getLeftHandSide())).visit(doBlock.getBody());
		for (SignalLike signalLike : signalLikes) {
			Predicate<ProcessedExpression> targetMatcher = expression ->
				(expression instanceof SignalLikeReference) && ((SignalLikeReference) expression).getDefinition() == signalLike;
			validate(signalLike.getName(), targetMatcher, signalLike.getProcessedDataType());
		}
		for (InstancePortReference reference : instancePortReferences) {
			String name = reference.getModuleInstance().getName() + '.' + reference.getPort().getName();
			Predicate<ProcessedExpression> targetMatcher = expression ->
				(expression instanceof InstancePortReference) && ((InstancePortReference) expression).isSameAs(reference);
			validate(name, targetMatcher, reference.getDataType());
		}
	}

	protected void collectTargets(ProcessedExpression destination) {
		if (destination instanceof SignalLikeReference) {
			signalLikes.add(((SignalLikeReference) destination).getDefinition());
		} else if (destination instanceof InstancePortReference) {
			instancePortReferences.add((InstancePortReference) destination);
		} else if (destination instanceof ProcessedIndexSelection) {
			collectTargets(((ProcessedIndexSelection) destination).getContainer());
		} else if (destination instanceof ProcessedRangeSelection) {
			collectTargets(((ProcessedRangeSelection) destination).getContainer());
		} else if (destination instanceof ProcessedBinaryOperation) {
			ProcessedBinaryOperation binaryOperation = (ProcessedBinaryOperation) destination;
			if (binaryOperation.getOperator() == ProcessedBinaryOperator.VECTOR_CONCAT) {
				collectTargets(binaryOperation.getLeftOperand());
				collectTargets(binaryOperation.getRightOperand());
			}
		}
	}

	private void validate(String name, Predicate<ProcessedExpression> targetMatcher, ProcessedDataType type) {
		if (type.getFamily() == ProcessedDataType.Family.BIT) {
			boolean assigns = new BranchVisitor<>(false, (x, y) -> x | y, (x, y) -> x & y,
				assignment -> assignsBit(assignment.getLeftHandSide(), targetMatcher)).visit(doBlock.getBody());
			if (!assigns) {
				sidekick.onError(doBlock.getBody().getErrorSource(), name + " is not assigned in all branches");
			}
		} else if (type.getFamily() == ProcessedDataType.Family.VECTOR) {
			int width = ((ProcessedDataType.Vector) type).getSize();
			BitSet bits = new BranchVisitor<>(new BitSet(), this::bitOr, this::bitAnd,
				assignment -> assignsVector(assignment.getLeftHandSide(), targetMatcher)).visit(doBlock.getBody());
			if (bits.isEmpty()) {
				sidekick.onError(doBlock.getBody().getErrorSource(), name + " is not assigned in all branches");
			} else {
				bits.flip(0, width);
				if (!bits.isEmpty()) {
					sidekick.onError(doBlock.getBody().getErrorSource(), "not all bits of " + name + " are assigned in all branches");
				}
			}
		}
	}

	private BitSet bitAnd(BitSet x, BitSet y) {
		BitSet result = (BitSet) x.clone();
		result.and(y);
		return result;
	}

	private BitSet bitOr(BitSet x, BitSet y) {
		BitSet result = (BitSet) x.clone();
		result.or(y);
		return result;
	}

	// TODO simplified for now: we don't properly check that all bits get assigned, but rather just say that everything
	// that appears in a left-hand side is fully assigned.

	protected boolean assignsBit(ProcessedExpression expression, Predicate<ProcessedExpression> targetMatcher) {
		ProcessedDataType type = assignsInternal(expression, targetMatcher);
		return (type != null);
	}

	protected BitSet assignsVector(ProcessedExpression expression, Predicate<ProcessedExpression> targetMatcher) {
		ProcessedDataType type = assignsInternal(expression, targetMatcher);
		BitSet result = new BitSet();
		if (type != null && type.getFamily() == ProcessedDataType.Family.VECTOR) {
			int width = ((ProcessedDataType.Vector) type).getSize();
			result.set(0, width);
		}
		return result;
	}

	private static ProcessedDataType assignsInternal(ProcessedExpression expression, Predicate<ProcessedExpression> targetMatcher) {
		if (targetMatcher.test(expression)) {
			return expression.getDataType();
		} else if (expression instanceof ProcessedIndexSelection) {
			return assignsInternal(((ProcessedIndexSelection) expression).getContainer(), targetMatcher);
		} else if (expression instanceof ProcessedRangeSelection) {
			return assignsInternal(((ProcessedRangeSelection) expression).getContainer(), targetMatcher);
		} else if (expression instanceof ProcessedBinaryOperation) {
			ProcessedBinaryOperation binaryOperation = (ProcessedBinaryOperation) expression;
			if (binaryOperation.getOperator() == ProcessedBinaryOperator.VECTOR_CONCAT) {
				ProcessedDataType type = assignsInternal(binaryOperation.getLeftOperand(), targetMatcher);
				if (type == null) {
					type = assignsInternal(binaryOperation.getRightOperand(), targetMatcher);
				}
				return type;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
