package name.martingeisse.mahdl.compiler.model;

import name.martingeisse.mahdl.common.processor.definition.SignalLike;
import name.martingeisse.mahdl.common.processor.expression.InstancePortReference;
import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.SignalLikeReference;
import name.martingeisse.mahdl.common.processor.statement.*;
import name.martingeisse.mahdl.compiler.CompilerContext;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public final class ContinuousDoBlockInfo extends DoBlockInfo {

	private final SortedSet<SignalLike> signalLikes;
	private final SortedSet<InstancePortReference> instancePortReferences;

	public ContinuousDoBlockInfo(String name, ProcessedDoBlock doBlock) {
		super(name, doBlock);
		this.signalLikes = new TreeSet<>();
		this.instancePortReferences = new TreeSet<>();
		analyze(doBlock.getBody());
	}

	public SortedSet<SignalLike> getSignalLikes() {
		return signalLikes;
	}

	public SortedSet<InstancePortReference> getInstancePortReferences() {
		return instancePortReferences;
	}

	@Override
	protected void analyzeAssignmentTo(SignalLike destination) {
		signalLikes.add(destination);
	}

	@Override
	protected void analyzeAssignmentTo(InstancePortReference destination) {
		instancePortReferences.add(destination);
	}

	public String findLocalClockSource(InstancePortReference target) {
		return findLocalClockSource(target, getDoBlock().getBody());
	}

	private String findLocalClockSource(InstancePortReference target, ProcessedStatement statement) {
		if (statement instanceof ProcessedBlock) {

			String result = null;
			for (ProcessedStatement child : ((ProcessedBlock) statement).getStatements()) {
				String childResult = findLocalClockSource(target, child);
				if (childResult != null) {
					// later assignments overwrite earlier ones
					result = childResult;
				}
			}
			return result;

		} else if (statement instanceof ProcessedAssignment) {

			ProcessedAssignment assignment = (ProcessedAssignment) statement;
			ProcessedExpression leftHandSide = assignment.getLeftHandSide();
			ProcessedExpression rightHandSide = assignment.getRightHandSide();
			if (leftHandSide instanceof InstancePortReference) {
				InstancePortReference portReference = (InstancePortReference) leftHandSide;
				if (portReference.isSameAs(target)) {
					// assignment to the right clock port, so check if the source is a simple signal
					if (rightHandSide instanceof SignalLikeReference) {
						return ((SignalLikeReference) rightHandSide).getDefinition().getName();
					} else {
						markClockSourceUnsupported(target, statement);
						return null;
					}
				}
			}
			return null;

		} else if (statement instanceof ProcessedIf || statement instanceof ProcessedSwitchStatement) {

			markClockSourceUnsupported(target, statement);
			return null;

		} else {

			return null;

		}
	}

	private void markClockSourceUnsupported(InstancePortReference target, ProcessedStatement statement) {
		if (statement instanceof ProcessedBlock) {

			for (ProcessedStatement child : ((ProcessedBlock) statement).getStatements()) {
				markClockSourceUnsupported(target, child);
			}

		} else if (statement instanceof ProcessedAssignment) {

			ProcessedAssignment assignment = (ProcessedAssignment) statement;
			ProcessedExpression leftHandSide = assignment.getLeftHandSide();
			if (leftHandSide instanceof InstancePortReference) {
				InstancePortReference portReference = (InstancePortReference) leftHandSide;
				if (portReference.isSameAs(target)) {
					CompilerContext.get().reportError(statement, "unsupported clock source");
				}
			}

		} else if (statement instanceof ProcessedIf) {

			ProcessedIf processedIf = (ProcessedIf) statement;
			markClockSourceUnsupported(target, processedIf.getThenBranch());
			markClockSourceUnsupported(target, processedIf.getElseBranch());

		} else if (statement instanceof ProcessedSwitchStatement) {

			ProcessedSwitchStatement processedSwitchStatement = (ProcessedSwitchStatement) statement;
			for (ProcessedSwitchStatement.Case aCase : processedSwitchStatement.getCases()) {
				markClockSourceUnsupported(target, aCase.getBranch());
			}
			if (processedSwitchStatement.getDefaultBranch() != null) {
				markClockSourceUnsupported(target, processedSwitchStatement.getDefaultBranch());
			}

		}
	}

}
