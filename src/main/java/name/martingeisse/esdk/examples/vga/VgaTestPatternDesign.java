/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.block.RtlClockedBlock;
import name.martingeisse.esdk.core.rtl.block.RtlProceduralVectorSignal;
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
public class VgaTestPatternDesign extends Design {

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final RtlOutputPin r;
	private final RtlOutputPin g;
	private final RtlOutputPin b;
	private final RtlOutputPin hsync;
	private final RtlOutputPin vsync;

	public VgaTestPatternDesign() {
		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(clockPin(realm));
		VgaTimer vgaTimer = new VgaTimer(clock);

		RtlBitSignal active = vgaTimer.getBlank().not();
		r = vgaPin(realm, "H14", vgaTimer.getX().select(4).and(active));
		g = vgaPin(realm, "H15", vgaTimer.getY().select(4).and(active));
		b = vgaPin(realm, "G15", vgaTimer.getX().add(vgaTimer.getY()).select(5).and(active));
		hsync = vgaPin(realm, "F15", vgaTimer.getHsync());
		vsync = vgaPin(realm, "F14", vgaTimer.getVsync());
	}

	public RtlRealm getRealm() {
		return realm;
	}

	public RtlClockNetwork getClock() {
		return clock;
	}

	public RtlOutputPin getR() {
		return r;
	}

	public RtlOutputPin getG() {
		return g;
	}

	public RtlOutputPin getB() {
		return b;
	}

	public RtlOutputPin getHsync() {
		return hsync;
	}

	public RtlOutputPin getVsync() {
		return vsync;
	}

	private static RtlInputPin clockPin(RtlRealm realm) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVCMOS33");
		RtlInputPin pin = new RtlInputPin(realm);
		pin.setId("C9");
		pin.setConfiguration(new XilinxPinConfiguration());
		return pin;
	}

	private static RtlOutputPin vgaPin(RtlRealm realm, String id, RtlBitSignal outputSignal) {
		XilinxPinConfiguration configuration = new XilinxPinConfiguration();
		configuration.setIostandard("LVTTL");
		configuration.setDrive(8);
		configuration.setSlew(XilinxPinConfiguration.Slew.FAST);
		RtlOutputPin pin = new RtlOutputPin(realm);
		pin.setId(id);
		pin.setConfiguration(new XilinxPinConfiguration());
		pin.setOutputSignal(outputSignal);
		return pin;
	}

}
