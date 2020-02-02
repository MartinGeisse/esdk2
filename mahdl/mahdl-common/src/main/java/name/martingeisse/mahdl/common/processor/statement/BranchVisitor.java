package name.martingeisse.mahdl.common.processor.statement;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 */
public final class BranchVisitor<R> {

	private final R emptyResult;
	private final BiFunction<R, R, R> sequenceOperator;
	private final BiFunction<R, R, R> branchOperator;
	private final Function<ProcessedAssignment, R> assignmentHandler;

	public BranchVisitor(R emptyResult, BiFunction<R, R, R> sequenceOperator, BiFunction<R, R, R> branchOperator, Function<ProcessedAssignment, R> assignmentHandler) {
		this.emptyResult = emptyResult;
		this.sequenceOperator = sequenceOperator;
		this.branchOperator = branchOperator;
		this.assignmentHandler = assignmentHandler;
	}

	public R getEmptyResult() {
		return emptyResult;
	}

	public BiFunction<R, R, R> getSequenceOperator() {
		return sequenceOperator;
	}

	public BiFunction<R, R, R> getBranchOperator() {
		return branchOperator;
	}

	public Function<ProcessedAssignment, R> getAssignmentHandler() {
		return assignmentHandler;
	}

	public R visit(ProcessedStatement statement) {
		return (statement == null ? emptyResult : statement.visitBranches(this));
	}

	public static BranchVisitor<Void> createAssignmentsWithoutResultVisitor(Consumer<ProcessedAssignment> assignmentConsumer) {
		BiFunction<Void, Void, Void> ignore = (x, y) -> null;
		return new BranchVisitor<>(null, ignore, ignore, assignment -> {
			assignmentConsumer.accept(assignment);
			return null;
		});
	}

}
