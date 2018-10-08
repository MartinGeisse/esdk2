/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.sim;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.examples.vga.test_renderer.TestRenderer;
import name.martingeisse.esdk.examples.vga.test_renderer.RtlFramebufferDisplay;

import javax.swing.*;

/**
 *
 */
public class RtlDirectReadSimulationMain {

	public static void main(String[] args) throws Exception {

		int widthBits = 7;
		int heightBits = 7;

		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		TestRenderer testRenderer = new TestRenderer(realm, clock, widthBits, heightBits);

		RtlFramebufferDisplay display = new RtlFramebufferDisplay(clock, widthBits, heightBits);
		testRenderer.connectDisplay(display);
		new RtlClockGenerator(clock, 10);

		JFrame frame = new JFrame("RTL Magic Framebuffer Display");
		frame.add(new RtlFramebufferDisplayPanel(display.getFramebuffer().getMatrix(), 7, 7));
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		new IntervalItem(design, 5, 10_000, frame::repaint);
		design.simulate();
	}

}
