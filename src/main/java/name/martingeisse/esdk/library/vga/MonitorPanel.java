/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.library.vga;

import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;

import javax.swing.*;
import java.awt.*;

/**
 * Use {@link #getMonitor()} to obtain the monitor and set the RGB and sync signals there.
 */
public class MonitorPanel extends JPanel {

	private final Monitor monitor;
	private final BufferedImageOutput imageOutput;

	public MonitorPanel(RtlClockNetwork clockNetwork, int width, int height) {
		super(false);
		imageOutput = new BufferedImageOutput(width, height);
		monitor = new Monitor(clockNetwork);
		monitor.setImageDecoder(new ImageDecoder(width, height, imageOutput));
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
	}

	public Monitor getMonitor() {
		return monitor;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(imageOutput.getImage(), 0, 0, null);
	}

}
