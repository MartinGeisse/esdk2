/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public final class FramebufferDisplayPanel extends JPanel {

	private final BufferedImage framebuffer;

	public FramebufferDisplayPanel(BufferedImage framebuffer) {
		super(false);
		this.framebuffer = framebuffer;
		setSize(framebuffer.getWidth(), framebuffer.getHeight());
		setPreferredSize(new Dimension(framebuffer.getWidth(), framebuffer.getHeight()));
	}

	public BufferedImage getFramebuffer() {
		return framebuffer;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(framebuffer, 0, 0, null);
	}

}
