/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.switched_immediate_ramdac;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlMultiportMemory;
import name.martingeisse.esdk.core.rtl.memory.multiport.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.pin.RtlInputPin;
import name.martingeisse.esdk.core.rtl.pin.RtlOutputPin;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlConditionalVectorOperation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.XilinxPinConfiguration;
import name.martingeisse.esdk.examples.vga.VgaTimer;

/**
 *
 */
public class RtlRamdacDesign extends Design {

	public static final int WIDTH_BITS = 7;
	public static final int HEIGHT_BITS = 7;

	private final RtlRealm realm;
	private final RtlClockNetwork clock;
	private final TestRenderer testRenderer;
	private final VgaTimer vgaTimer;
	private final RtlVectorSignal dacAddressSignal;
	private final RtlMultiportMemory framebuffer;
	private final RtlSynchronousMemoryPort framebufferPort;

	private final RtlOutputPin r;
	private final RtlOutputPin g;
	private final RtlOutputPin b;
	private final RtlOutputPin hsync;
	private final RtlOutputPin vsync;

	public RtlRamdacDesign() {

		realm = new RtlRealm(this);
		clock = realm.createClockNetwork(clockPin(realm));
		testRenderer = new TestRenderer(realm, clock, WIDTH_BITS, HEIGHT_BITS);
		vgaTimer = new VgaTimer(clock);
		dacAddressSignal = new RtlConcatenation(realm, vgaTimer.getY().select(7, 1), vgaTimer.getX().select(7, 1));

		// Note: rows and columns of the frame are not rows and columns of the RAM. Instead, the RAM
		// has one row per pixel and 3 columns (bits) for the 3 color channels.
		framebuffer = new RtlMultiportMemory(getRealm(), 1 << (WIDTH_BITS + HEIGHT_BITS), 3);
		framebufferPort = framebuffer.createSynchronousPort(clock,
			RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
		framebufferPort.setWriteEnableSignal(testRenderer.getFramebufferWriteStrobe());
		framebufferPort.setWriteDataSignal(testRenderer.getFramebufferWriteData().select(2, 0));
		framebufferPort.setAddressSignal(new RtlConditionalVectorOperation(getRealm(), testRenderer.getFramebufferRamdacSwitch(),
			dacAddressSignal, testRenderer.getFramebufferWriteAddress()));

		// VGA interface
		RtlVectorSignal dacReadData = framebufferPort.getReadDataSignal();
		RtlBitSignal blank = vgaTimer.getBlank().or(vgaTimer.getX().select(8)).or(vgaTimer.getX().select(9))
			.or(vgaTimer.getY().select(8)).or(vgaTimer.getY().select(9));
		RtlBitSignal active = blank.not();
		r = vgaPin(realm, "H14", active.and(dacReadData.select(2)));
		g = vgaPin(realm, "H15", active.and(dacReadData.select(1)));
		b = vgaPin(realm, "G15", active.and(dacReadData.select(0)));
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
