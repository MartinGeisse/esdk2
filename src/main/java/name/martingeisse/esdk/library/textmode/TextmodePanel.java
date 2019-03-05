/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.textmode;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.library.vga.BufferedImageOutput;
import name.martingeisse.esdk.library.vga.ImageDecoder;
import name.martingeisse.esdk.library.vga.Monitor;

import javax.swing.*;
import java.awt.*;

import java.awt.image.BufferedImage;

/**
 *
 */
public class TextmodePanel extends JPanel {

	private final BufferedImage image;
	private final RtlMemory characterMatrix;
	private final RtlSynchronousMemoryPort characterMatrixPort;

	public TextmodePanel(RtlClockNetwork clock) {
		super(false);
		image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		setSize(640, 480);
		setPreferredSize(new Dimension(640, 480));
		characterMatrix = new RtlMemory(clock.getRealm(), 128 * 48, 8);
		characterMatrixPort = characterMatrix.createSynchronousPort(clock,
			RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
	}

	@Override
	protected void paintComponent(Graphics g) {
		for (int matrixY = 0; matrixY < 30; matrixY++) {
			int matrixRowBaseAddress = matrixY << 7;
			for (int matrixX = 0; matrixX < 80; matrixX++) {
				int asciiCode = characterMatrix.getMatrix().getRow(matrixRowBaseAddress + matrixX).getAsUnsignedInt();
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

	public RtlSynchronousMemoryPort getCharacterMatrixPort() {
		return characterMatrixPort;
	}

}
