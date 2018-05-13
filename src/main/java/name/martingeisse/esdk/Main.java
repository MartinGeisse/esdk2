/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk;

import name.martingeisse.esdk.rtl.RtlDesign;
import name.martingeisse.esdk.rtl.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		RtlDesign design = new RtlDesign();
		new ProjectGenerator(design, "EsdkTestbild", new File("ise"), "XC3S500E-FG320-4").generate();
	}

}
