package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.block.statement.target.RtlBitAssignmentTarget;
import name.martingeisse.esdk.core.rtl.block.statement.target.RtlVectorAssignmentTarget;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.util.vector.VectorValue;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 */
public class RtlStatementBuilder {

	private RtlStatementSequence currentSequence;
	private final Deque<RtlStatementSequence> parentSequences = new ArrayDeque<>();
	private final Deque<RtlWhenStatement> parentWhenStatements = new ArrayDeque<>();

	public RtlStatementBuilder(RtlStatementSequence sequence) {
		this.currentSequence = sequence;
	}

	public final RtlStatementBuilder assign(RtlBitAssignmentTarget destination, RtlBitSignal source) {
		currentSequence.assign(destination, source);
		return this;
	}

	public final RtlStatementBuilder assign(RtlBitAssignmentTarget destination, boolean value) {
		currentSequence.assign(destination, value);
		return this;
	}

	public final RtlStatementBuilder assign(RtlVectorAssignmentTarget destination, RtlVectorSignal source) {
		currentSequence.assign(destination, source);
		return this;
	}

	public final RtlStatementBuilder assign(RtlVectorAssignmentTarget destination, VectorValue value) {
		currentSequence.assign(destination, value);
		return this;
	}

	public final RtlStatementBuilder assignUnsigned(RtlVectorAssignmentTarget destination, int value) {
		currentSequence.assignUnsigned(destination, value);
		return this;
	}

	public final RtlStatementBuilder when(RtlBitSignal condition) {
		RtlWhenStatement whenStatement = currentSequence.when(condition);
		parentWhenStatements.push(whenStatement);
		parentSequences.push(currentSequence);
		currentSequence = whenStatement.getThenBranch();
		return this;
	}

	public final RtlStatementBuilder otherwise() {
		if (parentWhenStatements.isEmpty()) {
			throw new IllegalStateException("no parent when-statement");
		}
		RtlWhenStatement whenStatement = parentWhenStatements.peek();
		if (whenStatement.getThenBranch() != currentSequence) {
			throw new IllegalStateException("not in then-branch");
		}
		currentSequence = whenStatement.getOtherwiseBranch();
		return this;
	}

	public final RtlStatementBuilder endWhen() {
		if (parentWhenStatements.isEmpty()) {
			throw new IllegalStateException("no parent when-statement");
		}
		parentWhenStatements.pop();
		currentSequence = parentSequences.pop();
		return this;
	}

}
