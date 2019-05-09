/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.pico_blink;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlBuilder;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.core.util.vector.VectorValue;
import name.martingeisse.esdk.library.picoblaze.model.rtl.PicoblazeRtlWithAssociatedProgram;

/**
 *
 */
public class PicoBlinkDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final RtlInputPin slideSwitch;
	private final PicoblazeRtlWithAssociatedProgram cpu;
	private final RtlVectorSignal leds;

	public PicoBlinkDesign() {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(clockPin(realm));
		slideSwitch = slideSwitchPin(realm, "L13");
		cpu = new PicoblazeRtlWithAssociatedProgram(clock, getClass());
		cpu.setPortInputDataSignal(
			new RtlConcatenation(realm,
				new RtlVectorConstant(realm, VectorValue.of(7, 0)),
				slideSwitch
			)
		);
		leds = RtlBuilder.vectorRegister(clock, cpu.getOutputData(), cpu.getWriteStrobe());

		ledPin(realm, "F12", leds.select(0));
		ledPin(realm, "E12", leds.select(1));
		ledPin(realm, "E11", leds.select(2));
		ledPin(realm, "F11", leds.select(3));
		ledPin(realm, "C11", leds.select(4));
		ledPin(realm, "D11", leds.select(5));
		ledPin(realm, "E9", leds.select(6));
		ledPin(realm, "F9", leds.select(7));
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

	public RtlInputPin getSlideSwitch() {
		return slideSwitch;
	}

	private static RtlOutputPin ledPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		RtlOutputPin pin = ledPin(realm, id);
		pin.setOutputSignal(outputSignal);
		return pin;
	}

	private static RtlOutputPin ledPin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setSlew(XilinxPinConfiguration.Slew.SLOW);
		configuration.setDrive(8);
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId("C9");
		pin.setConfiguration(configuration);
		return pin;
	}

	private static RtlInputPin slideSwitchPin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setAdditionalInfo("PULLUP");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(configuration);
		return pin;
	}

}
