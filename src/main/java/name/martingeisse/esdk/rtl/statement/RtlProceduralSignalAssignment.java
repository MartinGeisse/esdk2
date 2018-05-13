/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.statement;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlProceduralSignal;
import name.martingeisse.esdk.rtl.RtlSignal;

/**
 *
 */
public abstract class RtlProceduralSignalAssignment extends RtlStatement {

	public RtlProceduralSignalAssignment(RtlDesign design) {
		super(design);
	}

	public abstract RtlProceduralSignal getDestination();
	public abstract RtlSignal getSource();

}
