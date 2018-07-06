/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.rtl.block;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.verilog.VerilogWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a clocked block of statements, equivalent to a pair of "initial" and "always"
 * blocks in Verilog. The "always" part is triggered by a clock network.
 * <p>
 * For a sequence of statements grouped as a single statement, commonly referred to as a "block"
 * inside compilers, see {@link RtlStatementSequence}.
 */
public final class RtlClockedBlock extends RtlItem {

	private final RtlClockNetwork clockNetwork;
	private final List<RtlProceduralSignal> proceduralSignals;
	private final RtlStatementSequence initializerStatements;
	private final RtlStatementSequence statements;

	public RtlClockedBlock(RtlDesign design, RtlClockNetwork clockNetwork) {
		super(design);
		checkSameDesign(clockNetwork);

		this.clockNetwork = clockNetwork;
		this.proceduralSignals = new ArrayList<>();
		this.initializerStatements = new RtlStatementSequence(design);
		this.statements = new RtlStatementSequence(design);

		DesignRegistrationKey key = new DesignRegistrationKey();
		design.registerBlock(key, this);
		key.valid = false;
	}

	void registerProceduralSignal(RtlProceduralSignal proceduralSignal) {
		proceduralSignals.add(proceduralSignal);
	}

	public RtlClockNetwork getClockNetwork() {
		return clockNetwork;
	}

	public Iterable<RtlProceduralSignal> getProceduralSignals() {
		return proceduralSignals;
	}

	public RtlStatementSequence getInitializerStatements() {
		return initializerStatements;
	}

	public RtlStatementSequence getStatements() {
		return statements;
	}

	public void printVerilogBlocks(VerilogWriter out) {

		out.startProceduralInitialBlock();
		initializerStatements.printVerilogStatements(out);
		out.endProceduralAlwaysBlock();

		out.startProceduralAlwaysBlock("posedge " + out.getSignalName(clockNetwork.getClockSignal()));
		getStatements().printVerilogStatements(out);
		out.endProceduralAlwaysBlock();

	}

	public RtlProceduralBitSignal createBit() {
		return new RtlProceduralBitSignal(getDesign(), this);
	}

	public RtlProceduralVectorSignal createVector(int width) {
		return new RtlProceduralVectorSignal(getDesign(), this, width);
	}

	public void execute() {
		statements.execute();
	}

	public void updateProceduralSignals(List<? super RtlProceduralSignal> changeCollector) {
		for (RtlProceduralSignal signal : proceduralSignals) {
			if (signal.updateValue()) {
				changeCollector.add(signal);
			}
		}
	}

	/**
	 * This class is used to ensure that {@link RtlDesign#registerBlock(DesignRegistrationKey, RtlClockedBlock)} isn't called except through the
	 * {@link RtlClockedBlock} constructor.
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
