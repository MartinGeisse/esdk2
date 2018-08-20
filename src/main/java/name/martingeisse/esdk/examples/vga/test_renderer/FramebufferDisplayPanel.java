/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public final class FramebufferDisplayPanel extends JPanel {

	private final BufferedImage framebuffer;
	private final FramebufferDisplay display;

	public FramebufferDisplayPanel(RtlClockNetwork clockNetwork, int width, int height, int widthBits) {
		super(false);
		framebuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		display = new FramebufferDisplay(clockNetwork, framebuffer, widthBits);
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
	}

	public BufferedImage getFramebuffer() {
		return framebuffer;
	}

	public FramebufferDisplay getDisplay() {
		return display;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(framebuffer, 0, 0, null);
	}

}
