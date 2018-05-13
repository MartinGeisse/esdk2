/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlBitOperation extends RtlItem implements RtlBitSignal {

	private final Operator operator;
	private final RtlBitSignal leftOperand;
	private final RtlBitSignal rightOperand;

	public RtlBitOperation(RtlDesign design, Operator operator, RtlBitSignal leftOperand, RtlBitSignal rightOperand) {
		super(design);
		this.operator = operator;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public Operator getOperator() {
		return operator;
	}

	public RtlBitSignal getLeftOperand() {
		return leftOperand;
	}

	public RtlBitSignal getRightOperand() {
		return rightOperand;
	}

	@Override
	public RtlItem getRtlItem() {
		return this;
	}

	public enum Operator {
		AND,
		OR,
		XOR,
		XNOR,
	}

}
