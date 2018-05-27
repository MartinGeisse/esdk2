/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.*;

/**
 *
 */
public abstract class RtlAssignment extends RtlStatement {

	public RtlAssignment(RtlDesign design) {
		super(design);
	}

	public abstract RtlAssignmentTarget getDestination();
	public abstract RtlSignal getSource();

	@Override
	public void printExpressionsDryRun(VerilogExpressionWriter expressionWriter) {
		expressionWriter.print(getSource(), VerilogDesignGenerator.VerilogExpressionNesting.ALL);
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		getDestination().printVerilogAssignmentTarget(out);
		out.getOut().print(" <= ");
		out.printExpression(getSource());
		out.getOut().println(";");
	}

}
