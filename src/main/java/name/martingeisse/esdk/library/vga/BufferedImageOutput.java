/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.vga;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class BufferedImageOutput implements ImageDecoder.Output {

	private final BufferedImage image;

	public BufferedImageOutput(int width, int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	@Override
	public void outputPixel(int x, int y, int r, int g, int b) {
		image.setRGB(x, y, ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff));
	}

	public BufferedImage getImage() {
		return image;
	}

}
