/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.library.vga.Monitor;
import name.martingeisse.esdk.library.vga.MonitorPanel;

import javax.swing.*;

/**
 *
 */
public class RtlRamdacSimulationMain {

	public static void main(String[] args) throws Exception {

		// create the main design
		RtlRamdacDesign design = new RtlRamdacDesign();
		RtlRealm realm = design.getRealm();
		RtlClockNetwork clock = design.getClock();

		// generate a clock signal
		new RtlClockGenerator(design.getClock(), 10);

		// connect the RAMDAC to a monitor
		MonitorPanel monitorPanel = new MonitorPanel(clock, 640 + 16 + 48, 480 + 10 + 33, 2);
		Monitor monitor = monitorPanel.getMonitor();
		monitor.setR(design.getR().getOutputSignal().repeat(8));
		monitor.setG(design.getG().getOutputSignal().repeat(8));
		monitor.setB(design.getB().getOutputSignal().repeat(8));
		monitor.setHsync(design.getHsync().getOutputSignal());
		monitor.setVsync(design.getVsync().getOutputSignal());

		// show the monitor in a GUI
		JFrame frame = new JFrame("RTL RAMDAC Framebuffer Display");
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new IntervalItem(design, 5, 10_000, frame::repaint);

		// run
		design.simulate();

	}

}
