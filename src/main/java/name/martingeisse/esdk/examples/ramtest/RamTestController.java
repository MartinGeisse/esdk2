/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

/**
 *
 */
public class RamTestController extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final PicoblazeRtlWithAssociatedProgram cpu;
	private final RtlVectorSignal leds;

	private final RtlBitSignal ramClockEnable;
	private final RtlBitSignal ramWriteEnable;
	private final RtlVectorSignalConnector ramAddress;
	private final RtlVectorSignalConnector ramReadData;
	private final RtlVectorSignalConnector ramWriteData;

	public RamTestController() {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(inPin(realm, "clock"));

		// CPU
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, RamTestController.class);

		// registers
		ramAddress = new RtlVectorSignalConnector(realm, 32);
		ramReadData = new RtlVectorSignalConnector(realm, 32);
		ramWriteData = new RtlVectorSignalConnector(realm, 32);

		// LEDs
		leds = RtlBuilder.vectorRegister(clock, cpu.getOutputData(), cpu.getWriteStrobe().and(cpu.getPortAddress().select(3)));
		outPin(realm, "led0", leds.select(0));
		outPin(realm, "led1", leds.select(1));
		outPin(realm, "led2", leds.select(2));
		outPin(realm, "led3", leds.select(3));
		outPin(realm, "led4", leds.select(4));
		outPin(realm, "led5", leds.select(5));
		outPin(realm, "led6", leds.select(6));
		outPin(realm, "led7", leds.select(7));

		// glue logic
		ramClockEnable = cpu.getWriteStrobe().and(cpu.getPortAddress().select(7));
		ramWriteEnable = cpu.getOutputData().select(0);
		ramAddress.setConnected(cpuWritableWordRegister(4));
		ramWriteData.setConnected(cpuWritableWordRegister(5));
		{
			RtlConditionChainVectorSignal chain = new RtlConditionChainVectorSignal(realm, 8);
			chain.when(cpu.getPortAddress().select(4), cpuReadableByteSelect(ramAddress));
			chain.when(cpu.getPortAddress().select(5), cpuReadableByteSelect(ramWriteData));
			chain.when(cpu.getPortAddress().select(6), cpuReadableByteSelect(ramReadData));
			chain.otherwise(RtlVectorConstant.ofUnsigned(realm, 8, 0));
			cpu.setPortInputDataSignal(chain);
		}

	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public RtlVectorSignal getLeds() {
		return leds;
	}

	public RtlVectorSignal getRamAddress() {
		return ramAddress;
	}

	public RtlVectorSignal getRamWriteData() {
		return ramWriteData;
	}

	public void setRamReadData(RtlVectorSignal readData) {
		ramReadData.setConnected(RtlBuilder.vectorRegister(clock, readData,
			cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)).and(cpu.getOutputData().select(0).not())));
	}

	public RtlBitSignal getRamClockEnable() {
		return ramClockEnable;
	}

	public RtlBitSignal getRamWriteEnable() {
		return ramWriteEnable;
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

	private static RtlOutputPin outPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		return pin;
	}

	private static RtlInputPin inPin(RtlRealm realm, String id) {
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		return pin;
	}

}
