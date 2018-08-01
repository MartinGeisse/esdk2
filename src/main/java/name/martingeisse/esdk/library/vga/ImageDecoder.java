/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.vga;

/**
 * The RGB values are expected to be in the range 0..255.
 */
public final class ImageDecoder {

	private final int width;
	private final int height;
	private final Output output;
	private int x;
	private int y;
	private boolean rowStarted;

	public ImageDecoder(int width, int height, Output output) {
		this.width = width;
		this.height = height;
		this.output = output;
		this.x = 0;
		this.y = 0;
		this.rowStarted = false;
	}

	public void consumePixel(int r, int g, int b, boolean hsync, boolean vsync) {
		if (vsync) {
			x = 0;
			y = 0;
			rowStarted = false;
			return;
		}
		if (hsync) {
			if (rowStarted) {
				x = 0;
				y++;
			}
			rowStarted = false;
			return;
		}
		rowStarted = true;
		if (x < width && y < height) {
			output.outputPixel(x, y, r, g, b);
		}
		x++;
	}

	public interface Output {
		void outputPixel(int x, int y, int r, int g, int b);
	}

}
