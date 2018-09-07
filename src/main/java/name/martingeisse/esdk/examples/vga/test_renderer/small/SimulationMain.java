/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.small;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.examples.vga.test_renderer.display.SimulatedFramebufferDisplayPanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class SimulationMain {

	public static void main(String[] args) throws Exception {

		BufferedImage framebuffer = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		TestRendererDesign design = new TestRendererDesign(framebuffer, 7);
		new RtlClockGenerator(design.getClock(), 10);

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