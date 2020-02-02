/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.mahdl.common.processor.statement;

import name.martingeisse.mahdl.common.processor.expression.ProcessedExpression;
import name.martingeisse.mahdl.common.processor.expression.TypeErrorException;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;
import name.martingeisse.mahdl.input.cm.CmNode;

/**
 *
 */
public final class ProcessedAssignment extends ProcessedStatement {

	private final ProcessedExpression leftHandSide;
	private final ProcessedExpression rightHandSide;

	public ProcessedAssignment(CmNode errorSource, ProcessedExpression leftHandSide, ProcessedExpression rightHandSide) throws TypeErrorException {
		super(errorSource);
		if (!(leftHandSide.getDataType() instanceof ProcessedDataType.Unknown)) {
			if (!(rightHandSide.getDataType() instanceof ProcessedDataType.Unknown)) {
				if (!leftHandSide.getDataType().equals(rightHandSide.getDataType())) {
					throw new TypeErrorException("invalid types for assignment: " + leftHandSide.getDataType() + " and " + rightHandSide.getDataType());
				}
			}
		}
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
	}

	public ProcessedExpression getLeftHandSide() {
		return leftHandSide;
	}

	public ProcessedExpression getRightHandSide() {
		return rightHandSide;
	}

	@Override
	public <R> R visitBranches(BranchVisitor<R> visitor) {
		return visitor.getAssignmentHandler().apply(this);
	}
	
}
