/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlProceduralBitSignal extends RtlProceduralSignal implements RtlBitSignal, RtlBitAssignmentTarget {

	public RtlProceduralBitSignal(RtlDesign design, RtlBlock block) {
		super(design, block);
	}

	@Override
	public void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printProceduralSignalName(this);
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.printProceduralSignalName(this);
	}

}
