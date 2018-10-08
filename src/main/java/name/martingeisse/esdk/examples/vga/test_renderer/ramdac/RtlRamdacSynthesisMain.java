/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.examples.vga.test_renderer.ramdac;

import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class RtlRamdacSynthesisMain {

	public static void main(String[] args) throws Exception {
		RtlRamdacDesign design = new RtlRamdacDesign();
		ProjectGenerator projectGenerator = new ProjectGenerator(design.getRealm(), "Ramdac",
			new File("ise/ramdac"), "XC3S500E-FG320-4");
		projectGenerator.addVerilogFile(new File("resource/external/kcpsm3.v"));
		projectGenerator.generate();
	}

}
