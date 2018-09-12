/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.*;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.examples.vga.VgaTimer;
import name.martingeisse.esdk.examples.vga.test_renderer.display.RtlFramebufferDisplay;
import name.martingeisse.esdk.examples.vga.test_renderer.display.RtlFramebufferDisplayPanel;
import name.martingeisse.esdk.library.vga.Monitor;
import name.martingeisse.esdk.library.vga.MonitorPanel;

import javax.swing.*;

/**
 *
 */
public class RtlRamdacSimulationMain {

	public static void main(String[] args) throws Exception {

		int widthBits = 7;
		int heightBits = 7;

		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		TestRenderer testRenderer = new TestRenderer(realm, clock, widthBits, heightBits);

		RtlFramebufferDisplay display = new RtlFramebufferDisplay(clock, widthBits, heightBits);
		testRenderer.connectDisplay(display);

		VgaTimer vgaTimer = new VgaTimer(clock);
		display.setDacAddressSignal(new RtlConcatenation(realm, vgaTimer.getY().select(7, 1), vgaTimer.getX().select(7, 1)));

		new RtlClockGenerator(clock, 10);

		RtlVectorSignal dacReadData = display.getDacReadDataSignal();
		RtlBitSignal blank = vgaTimer.getBlank().or(vgaTimer.getX().select(8)).or(vgaTimer.getX().select(9))
			.or(vgaTimer.getY().select(8)).or(vgaTimer.getY().select(9));
		MonitorPanel monitorPanel = new MonitorPanel(clock, 640 + 16 + 48, 480 + 10 + 33, 2);
		Monitor monitor = monitorPanel.getMonitor();
		monitor.setR(new RtlConditionalVectorOperation(realm, blank, RtlVectorConstant.ofUnsigned(realm, 8, 0), dacReadData.select(2).repeat(8)));
		monitor.setG(new RtlConditionalVectorOperation(realm, blank, RtlVectorConstant.ofUnsigned(realm, 8, 0), dacReadData.select(1).repeat(8)));
		monitor.setB(new RtlConditionalVectorOperation(realm, blank, RtlVectorConstant.ofUnsigned(realm, 8, 0), dacReadData.select(0).repeat(8)));
		monitor.setHsync(vgaTimer.getHsync());
		monitor.setVsync(vgaTimer.getVsync());

		JFrame frame = new JFrame("RTL RAMDAC Framebuffer Display");
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		new IntervalItem(design, 5, 10_000, frame::repaint);
		design.simulate();

	}

}
