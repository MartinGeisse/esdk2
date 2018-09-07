/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.display;

import name.martingeisse.esdk.core.util.Matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Predicate;

/**
 *
 */
public final class RtlFramebufferDisplayPanel extends JPanel {

	private final Matrix matrix;
	private final int widthBits;
	private final int width;
	private final int heightBits;
	private final int height;
	private final BufferedImage image;
	private final Predicate<Void> enable;

	public RtlFramebufferDisplayPanel(Matrix matrix, int widthBits, int heightBits, Predicate<Void> enable) {
		super(false);
		this.matrix = matrix;
		this.widthBits = widthBits;
		this.width = 1 << widthBits;
		this.heightBits = heightBits;
		this.height = 1 << heightBits;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.enable = enable;
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (enable.test(null)) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int pixelValue = matrix.getRow((y << widthBits) + x).getAsUnsignedInt();
					int rgb = (pixelValue & 4) != 0 ? 0xff0000 : 0;
					rgb |= (pixelValue & 2) != 0 ? 0xff00 : 0;
					rgb |= (pixelValue & 1) != 0 ? 0xff : 0;
					image.setRGB(x, y, rgb);
				}
			}
			g.drawImage(image, 0, 0, null);
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
		}
	}

}
