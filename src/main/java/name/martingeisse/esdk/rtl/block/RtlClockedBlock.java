/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.block;

import name.martingeisse.esdk.rtl.RtlClockNetwork;
import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.verilog.VerilogWriter;
import name.martingeisse.esdk.rtl.statement.RtlStatementSequence;

/**
 *
 */
public final class RtlClockedBlock extends RtlBlock {

	private final RtlClockNetwork clockNetwork;
	private final RtlStatementSequence initializerStatements;

	public RtlClockedBlock(RtlDesign design, RtlClockNetwork clockNetwork) {
		super(design);
		checkSameDesign(clockNetwork);
		this.clockNetwork = clockNetwork;
		this.initializerStatements = new RtlStatementSequence(design);
	}

	public RtlStatementSequence getInitializerStatements() {
		return initializerStatements;
	}

	public void printVerilogBlocks(VerilogWriter out) {

		out.startProceduralInitialBlock();
		initializerStatements.printVerilogStatements(out);
		out.endProceduralAlwaysBlock();

		out.startProceduralAlwaysBlock(out.getSignalName(clockNetwork.getClockSignal()));
		getStatements().printVerilogStatements(out);
		out.endProceduralAlwaysBlock();

	}

}
