/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlItem;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralBitSignal;
import name.martingeisse.esdk.core.rtl.block.statement.RtlConditionChainStatement;
import name.martingeisse.esdk.core.rtl.block.statement.RtlStatementSequence;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.EmptyVerilogContribution;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.VerilogContribution;
import name.martingeisse.esdk.library.bus.mybus.MybusSimpleMasterAdapter;
import name.martingeisse.esdk.library.util.DebugOutput;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

/**
 * TODO only the first RAM location gets written
 * TODO reading produces totally wrong results
 * TODO reading should be delayed by one clock (just add a register) to meet timing constraints
 */
public class RamTestController extends RtlItem {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final RtlBitSignal reset;
	private final PicoblazeRtlWithAssociatedProgram cpu;

	private final MybusSimpleMasterAdapter mybusMaster;

	private final RtlVectorSignalConnector ramAddressRegister;
	private final RtlVectorSignalConnector ramReadDataRegister;
	private final RtlVectorSignalConnector ramWriteDataRegister;

	private final RtlVectorSignal leds;

	public RamTestController(RtlRealm realm, RtlClockNetwork clock, RtlBitSignal reset) {
		super(realm);
		this.realm = realm;
		this.clock = clock;
		this.reset = reset;
		this.cpu = new PicoblazeRtlWithAssociatedProgram(clock, RamTestController.class);
		cpu.setResetSignal(reset);
		this.mybusMaster = new MybusSimpleMasterAdapter(realm);
		this.ramAddressRegister = new RtlVectorSignalConnector(realm, 32);
		this.ramReadDataRegister = new RtlVectorSignalConnector(realm, 32);
		this.ramWriteDataRegister = new RtlVectorSignalConnector(realm, 32);

		// LEDs
		leds = RtlBuilder.vectorRegister(clock, cpu.getOutputData(), cpu.getWriteStrobe().and(cpu.getPortAddress().select(3)));

		// Mybus interface
		{
			RtlClockedBlock block = new RtlClockedBlock(clock);
			RtlProceduralBitSignal mbCycle = block.createBit();
			RtlProceduralBitSignal mbWrite = block.createBit();
			RtlConditionChainStatement chain = block.getStatements().conditionChain();
			RtlStatementSequence startCycle = chain.when(cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)));
			startCycle.assign(mbCycle, true);
			startCycle.assign(mbWrite, cpu.getOutputData().select(0));
			chain.when(mybusMaster.getAckSignal()).assign(mbCycle, false);
			mybusMaster.setStrobeSignal(mbCycle);
			mybusMaster.setWriteEnableSignal(mbWrite);
		}
		mybusMaster.setAddressSignal(new RtlConcatenation(realm, ramAddressRegister.select(29, 0), RtlVectorConstant.ofUnsigned(realm, 2, 0)));
		mybusMaster.setWriteDataSignal(ramWriteDataRegister);

		// glue logic
		ramAddressRegister.setConnected(cpuWritableWordRegister(4));
		ramWriteDataRegister.setConnected(cpuWritableWordRegister(5));
		ramReadDataRegister.setConnected(RtlBuilder.vectorRegister(clock, mybusMaster.getReadDataSignal(),
			mybusMaster.getAckSignal().and(mybusMaster.getWriteEnableSignal().not())));
		{
			RtlConditionChainVectorSignal chain = new RtlConditionChainVectorSignal(realm, 8);
			chain.when(cpu.getPortAddress().select(4), cpuReadableByteSelect(ramAddressRegister));
			chain.when(cpu.getPortAddress().select(5), cpuReadableByteSelect(ramWriteDataRegister));
			chain.when(cpu.getPortAddress().select(6), cpuReadableByteSelect(ramReadDataRegister));
			chain.when(cpu.getPortAddress().select(7), new RtlConcatenation(realm, RtlVectorConstant.ofUnsigned(realm, 7, 0), mybusMaster.getStrobeSignal()));
			chain.otherwise(RtlVectorConstant.ofUnsigned(realm, 8, 0));
			// TODO cpu.setPortInputDataSignal(RtlBuilder.vectorRegister(clock, chain));
			cpu.setPortInputDataSignal(chain);
		}

		// debug output
		new DebugOutput(clock, cpu.getOutputData(), cpu.getWriteStrobe().and(cpu.getPortAddress().select(2)), value -> {
			if (value < 16) {
				System.out.println("DEBUG: 0" + Integer.toHexString(value));
			} else {
				System.out.println("DEBUG: " + Integer.toHexString(value));
			}
		});

	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public RtlVectorSignal getLeds() {
		return leds;
	}

	public MybusSimpleMasterAdapter getMybusMaster() {
		return mybusMaster;
	}

	private RtlVectorSignal cpuWritableWordRegister(int registerBit) {
		return new RtlConcatenation(realm, cpuWritableByteRegister(registerBit, 3), cpuWritableByteRegister(registerBit, 2),
			cpuWritableByteRegister(registerBit, 1), cpuWritableByteRegister(registerBit, 0));
	}

	private RtlVectorSignal cpuWritableByteRegister(int registerBit, int selectedByte) {
		RtlBitSignal enable = cpu.getWriteStrobe()
			.and(cpu.getPortAddress().select(registerBit)
				.and(cpu.getPortAddress().select(1, 0).compareEqual(selectedByte)));
		return RtlBuilder.vectorRegister(clock, cpu.getOutputData(), enable);
	}

	private RtlVectorSignal cpuReadableByteSelect(RtlVectorSignal wordSignal) {
		return new RtlShiftOperation(realm, RtlShiftOperation.Direction.RIGHT, wordSignal,
			new RtlConcatenation(realm, cpu.getPortAddress().select(1, 0), RtlVectorConstant.ofUnsigned(realm, 3, 0))
		).select(7, 0);
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
