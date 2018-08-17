package name.martingeisse.esdk.examples.vga.test_pattern;

import name.martingeisse.esdk.core.rtl.synthesis.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class SynthesisMain {

	public static void main(String[] args) throws Exception {
		VgaTestPatternDesign design = new VgaTestPatternDesign();
		new ProjectGenerator(design.getRealm(), "VgaTestPattern", new File("ise/vga_test_pattern"), "XC3S500E-FG320-4").generate();
	}

}
