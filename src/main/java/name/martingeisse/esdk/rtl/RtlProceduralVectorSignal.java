/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl;

/**
 *
 */
public final class RtlProceduralVectorSignal extends RtlProceduralSignal implements RtlVectorSignal, RtlVectorAssignmentTarget {

	private final int width;

	public RtlProceduralVectorSignal(RtlDesign design, RtlBlock block, int width) {
		super(design, block);
		this.width = width;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void printVerilogAssignmentTarget(VerilogWriter out) {
		TODO
	}

	@Override
	public void printVerilogExpression(VerilogExpressionWriter out) {
		// the VerilogExpressionWriter should not call this method again but print
		// the allocated signal name instead, so this won't go into an infinite loop
		out.print(this);
	}

}
