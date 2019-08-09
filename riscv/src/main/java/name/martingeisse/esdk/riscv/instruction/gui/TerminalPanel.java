/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.riscv.instruction.gui;

import name.martingeisse.esdk.riscv.common.terminal.CharacterGenerator;
import name.martingeisse.esdk.riscv.common.terminal.KeyCodeTranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 *
 */
public class TerminalPanel extends JPanel {

	private final BufferedImage image;
	private final byte[] characterMatrix;
	private final LinkedList<Byte> inputBuffer = new LinkedList<>();

	public TerminalPanel() {
		super(false);
		setFocusable(true);
		image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		setSize(640, 480);
		setPreferredSize(new Dimension(640, 480));
		characterMatrix = new byte[128 * 32];
		addKeyListener(new KeyAdapter() {

			private final KeyCodeTranslator translator = new KeyCodeTranslator();

			@Override
			public void keyPressed(KeyEvent e) {
				handle(translator.translate(e.getKeyCode(), false));
			}

			@Override
			public void keyReleased(KeyEvent e) {
				handle(translator.translate(e.getKeyCode(), true));
			}

			private void handle(byte[] bytes) {
				if (bytes != null) {
					for (byte b : bytes) {
						inputBuffer.offer(b);
					}
				}
			}

		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		for (int matrixY = 0; matrixY < 30; matrixY++) {
			int matrixRowBase = matrixY << 7;
			for (int matrixX = 0; matrixX < 80; matrixX++) {
				int asciiCode = characterMatrix[matrixRowBase + matrixX] & 0xff;
				byte[] characterPixels = CharacterGenerator.CHARACTER_DATA[asciiCode];
				for (int pixelY = 0; pixelY < 16; pixelY++) {
					byte pixelRow = characterPixels[pixelY];
					for (int pixelX = 0; pixelX < 8; pixelX++) {
						int rgb = ((pixelRow & 1) == 0 ? 0 : 0xc0c0c0);
						image.setRGB((matrixX << 3) + pixelX, (matrixY << 4) + pixelY, rgb);
						pixelRow >>= 1;
					}
				}
			}
		}
		g.drawImage(image, 0, 0, null);
	}

	public void setCharacter(int x, int y, byte b) {
		if (x < 0 || x >= 128 || y < 0 || y >= 32) {
			throw new RuntimeException("invalid character position: " + x + ", " + y);
		}
		characterMatrix[(y << 7) + x] = b;
	}

	public byte readInput() {
		return inputBuffer.isEmpty() ? 0 : inputBuffer.poll();
	}

}
