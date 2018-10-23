/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.ramtest;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlBitSignalConnector;
import name.martingeisse.esdk.core.rtl.signal.connector.RtlVectorSignalConnector;
import name.martingeisse.esdk.library.bus.wishbone.WishboneSimpleMaster;
import name.martingeisse.esdk.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

/**
 *
 */
public class RamTestController extends Design {

	private final RtlRealm realm = new RtlRealm(this);
	private final RtlClockNetwork clock = realm.createClockNetwork(inPin(realm, "clock"));
	private final PicoblazeRtlWithAssociatedProgram cpu = new PicoblazeRtlWithAssociatedProgram(clock, RamTestController.class);

	private final RtlVectorSignalConnector ramAddressRegister = new RtlVectorSignalConnector(realm, 32);
	private final RtlVectorSignalConnector ramReadDataRegister = new RtlVectorSignalConnector(realm, 32);
	private final RtlVectorSignalConnector ramWriteDataRegister = new RtlVectorSignalConnector(realm, 32);

	private final RtlBitSignalConnector wbCycle = new RtlBitSignalConnector(realm);
	private final RtlBitSignalConnector wbWrite = new RtlBitSignalConnector(realm);
	private final RtlBitSignalConnector wbAck = new RtlBitSignalConnector(realm);
	private final RtlVectorSignalConnector wbAddress = new RtlVectorSignalConnector(realm, 32);
	private final RtlVectorSignalConnector wbReadData = new RtlVectorSignalConnector(realm, 32);
	private final RtlVectorSignalConnector wbWriteData = new RtlVectorSignalConnector(realm, 32);
	private final WishboneSimpleMaster wishboneMaster;

	private final RtlVectorSignal leds;

	public RamTestController() {

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
		ramAddressRegister.setConnected(cpuWritableWordRegister(4));
		ramWriteDataRegister.setConnected(cpuWritableWordRegister(5));
		ramReadDataRegister.setConnected(RtlBuilder.vectorRegister(clock, wbReadData, wbAck.and(wbWrite.not())));
		{
			RtlConditionChainVectorSignal chain = new RtlConditionChainVectorSignal(realm, 8);
			chain.when(cpu.getPortAddress().select(4), cpuReadableByteSelect(ramAddressRegister));
			chain.when(cpu.getPortAddress().select(5), cpuReadableByteSelect(ramWriteDataRegister));
			chain.when(cpu.getPortAddress().select(6), cpuReadableByteSelect(ramReadDataRegister));
			chain.otherwise(RtlVectorConstant.ofUnsigned(realm, 8, 0));
			cpu.setPortInputDataSignal(chain);
		}

		// Wishbone interface
		{
			// TODO consider an RtlConditionChainBitRegister -- would simplify this case
			RtlConditionChainBitSignal chain = new RtlConditionChainBitSignal(realm);
			wbCycle.setConnected(chain);
			chain.when(cpu.getWriteStrobe().and(cpu.getPortAddress().select(7)), true);
			chain.when(wbAck, false);
			chain.otherwise(wbCycle);
		}
		wbWrite.setConnected(RtlBuilder.bitRegister(clock, cpu.getOutputData().select(0), cpu.getWriteStrobe().and(cpu.getPortAddress().select(7))));
		wbAddress.setConnected(ramAddressRegister);
		wbWriteData.setConnected(ramWriteDataRegister);
		wishboneMaster = new WishboneSimpleMaster() {

			@Override
			public RtlBitSignal getCycleStrobeSignal() {
				return wbCycle;
			}

			@Override
			public RtlBitSignal getWriteEnableSignal() {
				return wbWrite;
			}

			@Override
			public RtlVectorSignal getAddressSignal() {
				return wbAddress;
			}

			@Override
			public RtlVectorSignal getWriteDataSignal() {
				return wbWriteData;
			}

			@Override
			public void setReadDataSignal(RtlVectorSignal wbReadDataSignal) {
				wbReadData.setConnected(wbReadDataSignal);
			}

			@Override
			public void setAckSignal(RtlBitSignal wbAckSignal) {
				wbAck.setConnected(wbAckSignal);
			}
		};

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

	public WishboneSimpleMaster getWishboneMaster() {
		return wishboneMaster;
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
