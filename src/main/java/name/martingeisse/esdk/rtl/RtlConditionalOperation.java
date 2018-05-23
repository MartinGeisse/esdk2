/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public abstract class RtlConditionalOperation extends RtlItem implements RtlSignal {

	private final RtlBitSignal condition;

	public RtlConditionalOperation(RtlDesign design, RtlBitSignal condition) {
		super(design);
		this.condition = condition;
	}

	public final RtlBitSignal getCondition() {
		return condition;
	}

	public abstract RtlSignal getOnTrue();

	public abstract RtlSignal getOnFalse();

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(condition, VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" ? ");
		out.print(getOnTrue(), VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" : ");
		out.print(getOnFalse(), VerilogDesignGenerator.VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
