/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.rtl.block;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.RtlItem;
import name.martingeisse.esdk.rtl.verilog.VerilogWriter;
import name.martingeisse.esdk.rtl.statement.RtlStatementSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements an asynchronous or clocked block of statements, equivalent to an "initial" or "always"
 * block in Verilog. For a sequence of statements grouped as a single statement, commonly referred to as a "block"
 * inside compilers, see {@link RtlStatementSequence}.
 */
public abstract class RtlBlock extends RtlItem {

	private final List<RtlProceduralSignal> proceduralSignals = new ArrayList<>();
	private final RtlStatementSequence statements;

	public RtlBlock(RtlDesign design) {
		super(design);
		statements = new RtlStatementSequence(design);

		DesignRegistrationKey key = new DesignRegistrationKey();
		design.registerBlock(key, this);
		key.valid = false;
	}

	void registerProceduralSignal(RtlProceduralSignal proceduralSignal) {
		proceduralSignals.add(proceduralSignal);
	}

	public Iterable<RtlProceduralSignal> getProceduralSignals() {
		return proceduralSignals;
	}

	public RtlStatementSequence getStatements() {
		return statements;
	}

	public abstract void printVerilogBlocks(VerilogWriter out);

	public RtlProceduralBitSignal createBit() {
		return new RtlProceduralBitSignal(getDesign(), this);
	}

	public RtlProceduralVectorSignal createVector(int width) {
		return new RtlProceduralVectorSignal(getDesign(), this, width);
	}

	/**
	 * This class is used to ensure that {@link RtlDesign#registerBlock(DesignRegistrationKey, RtlBlock)} isn't called except through the
	 * {@link RtlBlock} constructor.
	 */
	public static final class DesignRegistrationKey {

		private boolean valid = true;

		private DesignRegistrationKey() {
		}

		public boolean isValid() {
			return valid;
		}

	}

}
