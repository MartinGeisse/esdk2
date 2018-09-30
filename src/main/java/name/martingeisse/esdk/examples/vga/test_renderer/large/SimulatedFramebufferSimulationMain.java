/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.signal.RtlBitConstant;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.examples.vga.test_renderer.display.SimulatedFramebufferDisplay;
import name.martingeisse.esdk.examples.vga.test_renderer.display.SimulatedFramebufferDisplayPanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class SimulatedFramebufferSimulationMain {

	public static void main(String[] args) throws Exception {

		int widthBits = 10;
		int heightBits = 9;

		Design design = new Design();
		RtlRealm realm = new RtlRealm(design);
		RtlClockNetwork clock = realm.createClockNetwork(new RtlBitConstant(realm, false));
		TestRenderer testRenderer = new TestRenderer(realm, clock, widthBits, heightBits);

		BufferedImage framebuffer = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		SimulatedFramebufferDisplay display = new SimulatedFramebufferDisplay(clock, framebuffer, 10);
		testRenderer.connectDisplay(display);
		new RtlClockGenerator(clock, 10);

		JFrame frame = new JFrame("Framebuffer Display");
		frame.add(new SimulatedFramebufferDisplayPanel(framebuffer));
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		new IntervalItem(design, 5, 10_000, frame::repaint);
		design.simulate();
	}

}
