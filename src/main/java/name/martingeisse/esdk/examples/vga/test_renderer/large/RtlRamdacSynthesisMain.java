/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.large;

import name.martingeisse.esdk.core.model.items.IntervalItem;
import name.martingeisse.esdk.core.rtl.RtlClockNetwork;
import name.martingeisse.esdk.core.rtl.RtlRealm;
import name.martingeisse.esdk.core.rtl.simulation.RtlClockGenerator;
import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;
import name.martingeisse.esdk.library.vga.Monitor;
import name.martingeisse.esdk.library.vga.MonitorPanel;

import javax.swing.*;
import java.io.File;

/**
 *
 */
public class RtlRamdacSynthesisMain {

	public static void main(String[] args) throws Exception {
		RtlRamdacDesign design = new RtlRamdacDesign();
		new ProjectGenerator(design.getRealm(), "Ramdac", new File("ise/ramdac"), "XC3S500E-FG320-4").generate();
	}

}
