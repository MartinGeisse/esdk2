/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.examples.vga.test_renderer.display.RtlMagicFramebufferDisplay;
import name.martingeisse.esdk.examples.vga.test_renderer.display.RtlFramebufferDisplayPanel;

import javax.swing.*;

/**
 *
 */
public class RtlMagicSimulationMain {

	public static void main(String[] args) throws Exception {

		TestRendererDesign design = new TestRendererDesign(7, 7);
		RtlMagicFramebufferDisplay display = new RtlMagicFramebufferDisplay(design.getClock(), 7, 7);
		design.connectDisplay(display);
		new RtlClockGenerator(design.getClock(), 10);

		JFrame frame = new JFrame("RTL Magic Framebuffer Display");
		frame.add(new RtlFramebufferDisplayPanel(display.getFramebuffer().getMatrix(), 7, 7, v -> true));
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		new IntervalItem(design, 5, 10_000, frame::repaint);
		design.simulate();
	}

}
