/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.moving_light;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorRegister;
import name.martingeisse.esdk.core.rtl.block.statement.RtlWhenStatement;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlConditionalVectorOperation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;

/**
 *
 */
public class MovingLightDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clk;
	private final RtlVectorSignal leds;
	private final RtlInputPin slideSwitch;

	public MovingLightDesign() {
		realm = new RtlRealm(this);
		clk = realm.createClockNetwork(clockPin(realm));
		slideSwitch = slideSwitchPin(realm, "L13");

		RtlClockedBlock block = clk.createBlock();

		// register vector[24] prescaler = 0;
		// do (clk) {
		//   prescaler = prescaler + 1;
		// }
		RtlProceduralVectorRegister prescaler = block.createVector(24);
		block.getInitializerStatements().assignUnsigned(prescaler, 0);
		block.getStatements().assign(prescaler, prescaler.add(1));

		// register vector[8] leds = 1;
		// do (clk) {
		//   if (prescaler == 0) {
		//     leds = (slideSwitch ? leds[0] _ leds[7:1] : leds[6:0] _ leds[7]);
		//   }
		// }
		//
		RtlProceduralVectorRegister leds = block.createVector(8);
		block.getInitializerStatements().assignUnsigned(leds, 1);
		RtlWhenStatement whenPrescalerZero = block.getStatements().when(prescaler.compareEqual(0));
		whenPrescalerZero.getThenBranch().assign(leds,
			new RtlConditionalVectorOperation(realm, slideSwitch,
				new RtlConcatenation(realm, leds.select(0), leds.select(7, 1)),
				new RtlConcatenation(realm, leds.select(6, 0), leds.select(7))
			)
		);
		this.leds = leds;

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

	public RtlClockNetwork getClk() {
		return clk;
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
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId("C9");
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlInputPin slideSwitchPin(RtlRealm realm, String id) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setAdditionalInfo("PULLUP");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId(id);
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

}
