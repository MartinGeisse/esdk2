/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.riscv.experiment.terminal;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlClockedItem;
import name.martingeisse.esdk.core.rtl.memory.RtlMemory;
import name.martingeisse.esdk.core.rtl.memory.RtlSynchronousMemoryPort;
import name.martingeisse.esdk.core.rtl.signal.RtlBitSignal;
import name.martingeisse.esdk.core.rtl.signal.RtlVectorSignal;
import name.martingeisse.esdk.core.rtl.simulation.RtlSimulatedComputedVectorSignal;
import name.martingeisse.esdk.core.rtl.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.VectorValue;

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
	private final RtlMemory characterMatrix;
	private final RtlSynchronousMemoryPort characterMatrixPort;

	private final LinkedList<Byte> inputBuffer = new LinkedList<>();
	private final RtlVectorSignal inputDataSignal;
	private RtlBitSignal inputAcknowledgeSignal;

	public TerminalPanel(RtlClockNetwork clock) {
		super(false);
		image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		setSize(640, 480);
		setPreferredSize(new Dimension(640, 480));
		characterMatrix = new RtlMemory(clock.getRealm(), 128 * 32, 8);
		characterMatrixPort = characterMatrix.createSynchronousPort(clock,
			RtlSynchronousMemoryPort.ReadSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.WriteSupport.SYNCHRONOUS,
			RtlSynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
		inputDataSignal = new RtlSimulatedComputedVectorSignal(clock.getRealm()) {

			@Override
			public int getWidth() {
				return 8;
			}

			@Override
			public VectorValue getValue() {
				Byte data = inputBuffer.peek();
				return VectorValue.of(8, data == null ? 0 : data.longValue());
			}

		};
		new RtlClockedItem(clock) {

			private boolean acknowledged;

			@Override
			public void computeNextState() {
				acknowledged = (inputAcknowledgeSignal != null) && inputAcknowledgeSignal.getValue();
			}

			@Override
			public void updateState() {
				if (acknowledged && !inputBuffer.isEmpty()) {
					inputBuffer.removeFirst();
				}
			}

			@Override
			public VerilogContribution getVerilogContribution() {
				throw newSynthesisNotSupportedException();
			}

		};
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
						inputBuffer.addLast(b);
					}
				}
			}

		});
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

	public RtlVectorSignal getInputDataSignal() {
		return inputDataSignal;
	}

	public RtlBitSignal getInputAcknowledgeSignal() {
		return inputAcknowledgeSignal;
	}

	public void setInputAcknowledgeSignal(RtlBitSignal inputAcknowledgeSignal) {
		this.inputAcknowledgeSignal = inputAcknowledgeSignal;
	}

}
