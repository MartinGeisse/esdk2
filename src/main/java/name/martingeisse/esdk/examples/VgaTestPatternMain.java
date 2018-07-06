package name.martingeisse.esdk.examples;

import name.martingeisse.esdk.core.model.Design;
import name.martingeisse.esdk.core.rtl.RtlDesign;
import name.martingeisse.esdk.core.rtl.xilinx.ProjectGenerator;

import java.io.File;

/**
 *
 */
public class VgaTestPatternMain {

	public static void main(String[] args) throws Exception {
		// TODO
		Design mainDesign = new Design();
		RtlDesign design = new RtlDesign(mainDesign);
		new ProjectGenerator(design, "EsdkTestbild", new File("ise"), "XC3S500E-FG320-4").generate();
	}

}
