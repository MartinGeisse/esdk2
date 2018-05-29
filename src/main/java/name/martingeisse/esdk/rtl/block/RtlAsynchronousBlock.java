/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.block;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.verilog.VerilogWriter;

/**
 *
 */
public final class RtlAsynchronousBlock extends RtlBlock {

	public RtlAsynchronousBlock(RtlDesign design) {
		super(design);
	}

	public void printVerilogBlocks(VerilogWriter out) {
		out.startProceduralAlwaysBlock("*");
		getStatements().printVerilogStatements(out);
		out.endProceduralAlwaysBlock();
	}

}
