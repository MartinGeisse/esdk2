/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

/**
 *
 */
public class HugeBlockRamSimulationMain {

	private static Design design;
	private static RtlRealm realm;
	private static RtlClockNetwork clock;
	private static PicoblazeRtlWithAssociatedProgram cpu;

	public static void main(String[] args) {

		// design
		design = new Design();
		realm = new RtlRealm(design);
		clock = realm.createClockNetwork(new RtlBitConstant(realm, false));

		// CPU
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, HugeBlockRamSimulationMain.class);

		// registers
		RtlVectorSignalConnector ramAddress = new RtlVectorSignalConnector(realm, 32);
		RtlVectorSignalConnector ramReadData = new RtlVectorSignalConnector(realm, 32);
		RtlVectorSignalConnector ramWriteData = new RtlVectorSignalConnector(realm, 32);

		// RAM
		RtlMemory ram = new RtlMemory(realm, 8 * 1024 * 1024, 32); // actually 16M x 16 DDR -> 8M x 32 SDR
		RtlSynchronousMemoryPort ramPort = ram.createSynchronousPort(clock, RtlSynchronousMemoryPort.ReadSupport.ASYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS, RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);

		// LEDs
		RtlVectorSignal leds = RtlBuilder.vectorRegister(clock, cpu.getOutputData(), cpu.getWriteStrobe().and(cpu.getPortAddress().select(3)));
		new IntervalItem(design, 10, 100_000_000, () -> { // 10 times per simulated second
			System.out.println(leds.getValue());
		});

		// glue logic
		RtlVectorSignal byteSelect = cpu.getPortAddress().select(1, 0);
		ramPort.setAddressSignal(ramAddress.select(25, 0));
		ramPort.setWriteDataSignal(ramWriteData);
		ramPort.setClockEnableSignal(cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)));
		ramPort.setWriteEnableSignal(cpu.getOutputData().select(0));
		ramAddress.setConnected(cpuWritableWordRegister(4));
		ramWriteData.setConnected(cpuWritableWordRegister(5));
		ramReadData.setConnected(RtlBuilder.vectorRegister(clock, ramPort.getReadDataSignal(),
			cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)).and(cpu.getOutputData().select(0).not())));
		{
			RtlConditionChainVectorSignal chain = new RtlConditionChainVectorSignal(realm, 8);
			chain.when(cpu.getPortAddress().select(4), cpuReadableByteSelect(ramAddress));
			chain.when(cpu.getPortAddress().select(5), cpuReadableByteSelect(ramWriteData));
			chain.when(cpu.getPortAddress().select(6), cpuReadableByteSelect(ramReadData));
			chain.otherwise(RtlVectorConstant.ofUnsigned(realm, 8, 0));
			cpu.setPortInputDataSignal(chain);
		}

		// simulation
		new RtlClockGenerator(clock, 10); // 100 MHz (10 ns) clock
		design.fire(design::stopSimulation, 20_000_000_000L);
		design.simulate();

	}

	private static RtlVectorSignal cpuWritableWordRegister(int registerBit) {
		return new RtlConcatenation(realm, cpuWritableByteRegister(registerBit, 3), cpuWritableByteRegister(registerBit, 2),
			cpuWritableByteRegister(registerBit, 1), cpuWritableByteRegister(registerBit, 0));
	}

	private static RtlVectorSignal cpuWritableByteRegister(int registerBit, int selectedByte) {
		RtlBitSignal enable = cpu.getWriteStrobe()
			.and(cpu.getPortAddress().select(registerBit)
				.and(cpu.getPortAddress().select(1, 0).compareEqual(selectedByte)));
		return RtlBuilder.vectorRegister(clock, cpu.getOutputData(), enable);
	}

	private static RtlVectorSignal cpuReadableByteSelect(RtlVectorSignal wordSignal) {
		return new RtlShiftOperation(realm, RtlShiftOperation.Direction.RIGHT, wordSignal,
			new RtlConcatenation(realm, cpu.getPortAddress().select(1, 0), RtlVectorConstant.ofUnsigned(realm, 3, 0))
		).select(7, 0);
	}

}
