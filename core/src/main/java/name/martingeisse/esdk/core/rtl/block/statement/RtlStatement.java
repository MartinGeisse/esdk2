/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block.statement;

import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogWriter;

/**
 *
 */
public abstract class RtlStatement extends RtlItem {

	public RtlStatement(RtlRealm realm) {
		super(realm);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	public abstract void execute();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	// statements are synthesized as part of the block they appear in -- there are currently no statements that need
	// any extra contribution
	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}


	public abstract void analyzeSignalUsage(SignalUsageConsumer consumer);

	public abstract void printVerilogStatements(VerilogWriter out);


}
