/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.vga;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlConcatenation;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorConstant;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockedSimulationItem;

/**
 * Simulates a VGA monitor in a very loose way:
 * - it accepts color values as digital values
 * - pixels are accepted based on the RTL clock and a clock-enable signal
 * - sync pulses may be as short as one clock cycle
 * - porches must be 0 clock cycles *after* taking the clock-enable into account
 * <p>
 * While this won't help in debugging sync problems, it should work well enough to debug the rest of the image
 * generation logic.
 */
public final class Monitor extends RtlClockedSimulationItem {

	private RtlVectorSignal r;
	private RtlVectorSignal g;
	private RtlVectorSignal b;
	private RtlBitSignal hsync;
	private RtlBitSignal vsync;
	private ImageDecoder imageDecoder;
	private int sampledR, sampledG, sampledB;
	private boolean sampledHsync, sampledVsync;

	public Monitor(RtlClockNetwork clockNetwork) {
		super(clockNetwork);
	}

	public void setR(RtlVectorSignal r) {
		this.r = adjustChannelWidth(r);
	}

	public void setG(RtlVectorSignal g) {
		this.g = adjustChannelWidth(g);
	}

	public void setB(RtlVectorSignal b) {
		this.b = adjustChannelWidth(b);
	}

	private RtlVectorSignal adjustChannelWidth(RtlVectorSignal channelSignal) {
		if (channelSignal.getWidth() == 8) {
			return channelSignal;
		} else if (channelSignal.getWidth() < 8) {
			RtlRealm realm = channelSignal.getRtlItem().getRealm();
			RtlVectorSignal filler = RtlVectorConstant.of(realm, 8 - channelSignal.getWidth(), 0);
			return new RtlConcatenation(realm, channelSignal, filler);
		} else {
			return channelSignal.select(channelSignal.getWidth() - 1, channelSignal.getWidth() - 8);
		}
	}

	public void setHsync(RtlBitSignal hsync) {
		this.hsync = hsync;
	}

	public void setVsync(RtlBitSignal vsync) {
		this.vsync = vsync;
	}

	public void setImageDecoder(ImageDecoder imageDecoder) {
		this.imageDecoder = imageDecoder;
	}

	@Override
	public void initializeSimulation() {
	}

	@Override
	public void computeNextState() {
		this.sampledR = r.getValue().getAsUnsignedInt();
		this.sampledG = g.getValue().getAsUnsignedInt();
		this.sampledB = b.getValue().getAsUnsignedInt();
		this.sampledHsync = hsync.getValue();
		this.sampledVsync = vsync.getValue();
	}

	@Override
	public void updateState() {
		imageDecoder.consumeDataUnit(sampledR, sampledG, sampledB, sampledHsync, sampledVsync);
	}

}
